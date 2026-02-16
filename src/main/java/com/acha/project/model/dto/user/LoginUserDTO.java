package com.acha.project.model.dto.user;

import lombok.Data;
import java.io.Serializable;

/**
 * 当前登录用户信息封装 (Context DTO)
 * 此对象放入 UserContext 中，贯穿整个请求生命周期
 */
@Data
public class LoginUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 当前使用的 Token (不带 Bearer 前缀)
     */
    private String token;
}
