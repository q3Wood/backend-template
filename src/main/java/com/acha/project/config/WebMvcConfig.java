package com.acha.project.config;

import com.acha.project.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 * 作用：注册拦截器，配置拦截路径
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册我们写的登录拦截器
        registry.addInterceptor(loginInterceptor)
                // 1. 拦截所有请求
                .addPathPatterns("/**")
                // 2. 放行不需要登录的接口 (白名单)
                .excludePathPatterns(
                        "/user/login",      // 登录
                        "/user/register",   // 注册
                        "/doc.html",        // Knife4j 文档页面
                        "/webjars/**",      // Knife4j 资源
                        "/v3/api-docs/**",  // Swagger 资源
                        "/favicon.ico"      // 网页图标
                );
    }
}