package com.acha.project.constant;

/**
 * Redis 常量类
 */
public interface RedisConstant {

    /**
     * 登录 Token 的 Key 前缀
     * 完整 Key: login:token:{token}
     */
    String LOGIN_TOKEN_KEY = "login:token:";

    /**
     * 登录用户 ID 的 Key 前缀 (用于单点登录互斥，可选)
     */
    String LOGIN_USER_ID_KEY = "login:userid:";

    /**
     * 默认过期时间 (单位：秒) - 只是备用，具体以 SecurityProperties 为准
     */
    Long LOGIN_TOKEN_TTL = 24 * 60 * 60L;
}
