package com.acha.project.service.impl;

import com.acha.project.mapper.UserMapper;
import com.acha.project.model.entity.User;
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
}