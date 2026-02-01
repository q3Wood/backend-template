package com.acha.project.service.impl;

import com.acha.project.mapper.UserMapper;
import com.acha.project.model.entity.User;
import com.acha.project.model.vo.user.UserVO;
import com.acha.project.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Service // 1. 标记这是一个 Spring 服务
@Slf4j   // 2. 打日志用的
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (!StringUtils.hasText(userAccount) || !StringUtils.hasText(userPassword) || !StringUtils.hasText(checkPassword)) {
            throw new RuntimeException("参数为空"); // 暂时用 RuntimeException，后面我们会统一处理异常
        }
        if (userAccount.length() < 3) {
            throw new RuntimeException("账号过短");
        }
        if (userPassword.length() < 6 || checkPassword.length() < 6) {
            throw new RuntimeException("密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new RuntimeException("两次密码不一致");
        }

        // 2. 检查账号是否重复 (利用 MyBatis Plus 的 QueryWrapper)
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("账号已存在");
        }

        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(userPassword); // 💡 注意：实际开发中这里必须加密（如 BCrypt），这里先明文存
        user.setUserName("普通用户");
        boolean saveResult = this.save(user); // 调用父类提供的 save 方法

        if (!saveResult) {
            throw new RuntimeException("注册失败，数据库错误");
        }

        return user.getId();
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword, jakarta.servlet.http.HttpServletRequest request) {
        // 1. 校验 (虽然 Controller 层有注解校验，Service 层最好也保留基础校验)
        if (!StringUtils.hasText(userAccount) || !StringUtils.hasText(userPassword)) {
            throw new RuntimeException("参数为空");
        }
        if (userAccount.length() < 4) {
            throw new RuntimeException("账号错误");
        }
        if (userPassword.length() < 8) {
            throw new RuntimeException("密码错误");
        }

        // 2. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        User user = this.getOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new RuntimeException("用户不存在或密码错误");
        }

        // 3. 校验密码
        // 目前是明文对比，以后我们要换成加密对比 (DigestUtils.md5DigestAsHex)
        if (!user.getUserPassword().equals(userPassword)) {
            log.info("user login failed, password error");
            throw new RuntimeException("用户不存在或密码错误");
        }

        // 4. 记录用户的登录态 (Session) 🔑 关键一步！
        // "USER_LOGIN_STATE" 是我们约定的 key，后面获取当前用户时要用
        request.getSession().setAttribute("USER_LOGIN_STATE", user);

        // 5. 数据脱敏 (把 User 转成 UserVO)
        UserVO userVO = new UserVO();
        // 也可以用 BeanUtils.copyProperties(user, userVO);
        userVO.setId(user.getId());
        userVO.setUserAccount(user.getUserAccount());
        userVO.setUserName(user.getUserName());
        userVO.setUserAvatar(user.getUserAvatar());
        userVO.setUserRole(user.getUserRole());
        userVO.setCreateTime(user.getCreateTime());

        return userVO;
    }

    @Override
    public UserVO getLoginUser(jakarta.servlet.http.HttpServletRequest request) {
        // 1. 从 Session 中获取用户
        // "USER_LOGIN_STATE" 要和之前登录时设置的 key 保持完全一致
        Object userObj = request.getSession().getAttribute("USER_LOGIN_STATE");
        User currentUser = (User) userObj;

        // 2. 检查是否登录
        if (currentUser == null || currentUser.getId() == null) {
            throw new RuntimeException("未登录");
        }

        // 3. 哪怕 Session 里有数据，也建议去数据库再查一次
        // 为什么？因为用户的角色、状态可能在管理员后台被改了，Session 里的数据可能是旧的。
        long userId = currentUser.getId();
        User user = this.getById(userId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 4. 脱敏返回
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUserAccount(user.getUserAccount());
        userVO.setUserName(user.getUserName());
        userVO.setUserAvatar(user.getUserAvatar());
        userVO.setUserRole(user.getUserRole());
        userVO.setCreateTime(user.getCreateTime());

        return userVO;
    }

    @Override
    public boolean userLogout(jakarta.servlet.http.HttpServletRequest request) {
        // 1. 移除登录态
        request.getSession().removeAttribute("USER_LOGIN_STATE");
        return true;
    }

}