package com.acha.project.constant;

/**
 * 用户相关常量
 */
public interface UserConstant {

    /**
     * 用户登录态键 (Header 中的 Key)
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 权限 Token (Header 中的 Key)
     */
    String TOKEN_HEADER = "Authorization";

    /**
     * Token 前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    //  权限角色
    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "admin";
}
