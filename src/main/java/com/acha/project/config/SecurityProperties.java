package com.acha.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 安全配置属性读取类
 * 对应 application.yml 中的 acha.security 前缀
 */
@Component
@ConfigurationProperties(prefix = "acha.security")
@Data // 需要 Lombok 生成 Getter/Setter，否则读不到值
public class SecurityProperties {

    /**
     * 密码盐值
     */
    private String salt;

    /**
     * JWT 秘钥
     */
    private String jwtKey;

    /**
     * Token 过期时间 (小时)
     */
    private Integer tokenTtlHours = 24; // 给个默认值
}