package com.acha.project.service;

import com.acha.project.model.entity.User;
import com.acha.project.model.vo.user.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 * 继承 IService<User> 后，自动拥有了基础的增删改查方法
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount   账号
     * @param userPassword  密码
     * @param checkPassword 校验密码
     * @return 新用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request      请求对象 (用来存 Session)
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, jakarta.servlet.http.HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request 请求对象
     * @return 脱敏后的用户信息
     */
    UserVO getLoginUser();

    /**
     * 用户注销
     * @param request 请求对象
     * @return 是否成功
     */
    boolean userLogout(jakarta.servlet.http.HttpServletRequest request);

}