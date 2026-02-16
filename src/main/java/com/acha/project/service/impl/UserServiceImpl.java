package com.acha.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;
import com.acha.project.common.ErrorCode;
import com.acha.project.common.UserContext;
import com.acha.project.exception.BusinessException;
import com.acha.project.config.SecurityProperties;
import com.acha.project.mapper.UserMapper;
import com.acha.project.model.dto.user.LoginUserDTO;
import com.acha.project.model.entity.User;
import com.acha.project.model.vo.user.UserVO;
import com.acha.project.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类 (JWT + Redis + Hutool 版本)
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserMapper userMapper;
    // 🧂 盐值：混在密码里，防止被彩虹表破解

    @Resource
    private SecurityProperties securityProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 基础校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        if (userAccount.length() < 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短");
        }
        // 2. 检查账号重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        // 3. 🔐 密码加密 (Hutool MD5)
        // 最终存进数据库的是：MD5(盐 + 原密码)
        String encryptPassword = BCrypt.hashpw(userPassword);//DigestUtil.md5Hex(salt + userPassword);

        // 4. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword); // 存密文
        user.setUserName("普通用户");
        user.setUserRole("user");

        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }

        return user.getId();
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 2. 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        User user = this.getOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 3. 🔐 校验密码
        // 把用户输入的密码同样加密一次，跟数据库里的密文比对

        if (!BCrypt.checkpw(userPassword, user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 4. 🎟️ 生成 JWT Token (Hutool)
        byte[] keyBytes = securityProperties.getJwtKey().getBytes(StandardCharsets.UTF_8);
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getId());
        payload.put("role", user.getUserRole());
        // 生成 Token
        String token = JWTUtil.createToken(payload, keyBytes);

        // 5. 💾 存入 Redis (Key: Token -> Value: UserJSON)
        // 格式 login:token:eyJxxx...
        String redisKey = "login:token:" + token;
        // Hutool JSONUtil 对象转字符串
        user.setUserPassword(null);
        String userJson = JSONUtil.toJsonStr(user);
        // 存入 Redis，设置 1 天过期
        Integer ttl = securityProperties.getTokenTtlHours();
        stringRedisTemplate.opsForValue().set(redisKey, userJson, ttl, TimeUnit.HOURS);

        // 6. 返回 VO (包含 Token)
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO); // Hutool 的 BeanUtil
        userVO.setToken(token); // 🚨 记得在 UserVO 里加这个字段！

        return userVO;
    }

    @Override
    public UserVO getLoginUser() {
        // 1. 直接从 ThreadLocal 获取 (拦截器已经帮我们从 Redis 取出来放进去了)
        LoginUserDTO currentUser = UserContext.get();

        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 2. 兜底策略：建议再去数据库查一次最新状态
        // 防止用户在 Redis 缓存期间被管理员封号或修改了角色
        User user = this.getById(currentUser.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 3. 转 VO
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public boolean userLogout(String token) {
        // 1. 校验 Token
        if (StrUtil.isNotBlank(token) && token.startsWith("Bearer ")) {
            // 2. 去除 Bearer 前缀
            token = token.substring(7);
            String redisKey = "login:token:" + token;
            stringRedisTemplate.delete(redisKey);
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePassword(String oldPassword, String newPassword, String checkPassword) {
        // 1. 校验参数
        LoginUserDTO currentUser = UserContext.get();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (StrUtil.hasBlank(oldPassword, newPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (!newPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次新密码不一致");
        }
        if (newPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码长度不能少于 8 位");
        }

        // 2. 查询数据库最新信息
        User user = this.getById(currentUser.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 3. 校验旧密码
        if (!BCrypt.checkpw(oldPassword, user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码错误");
        }

        // 4. 加密新密码并更新
        String encryptPassword = BCrypt.hashpw(newPassword);
        user.setUserPassword(encryptPassword);
        
        boolean updateResult = this.updateById(user);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改密码失败");
        }

        // 5. 修改密码成功后，强制清除 Token
        try {
            // 直接从 UserContext 获取 Token，不需要再去解析 Request 了
            String token = currentUser.getToken();
            if (StrUtil.isNotBlank(token)) {
                String redisKey = "login:token:" + token;
                stringRedisTemplate.delete(redisKey);
            }
        } catch (Exception e) {
            log.error("修改密码后清除Token失败", e);
        }
        
        return true;
    }
}