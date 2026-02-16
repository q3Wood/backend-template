package com.acha.project.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.acha.project.common.UserContext;
import com.acha.project.config.SecurityProperties;
import com.acha.project.model.dto.user.LoginUserDTO;
import com.acha.project.model.entity.User;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * 登录拦截器 (JWT + Redis 版本)
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SecurityProperties securityProperties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 1. 从 Header 获取 Token
        String token = request.getHeader("Authorization");

        // 2. 如果 Token 为空，直接拦截
        if (StrUtil.isBlank(token) || !token.startsWith("Bearer ")) {
            returnUnauthorized(response, "Token 格式错误或为空，请重新登录");
            return false;
        }

        token = token.substring(7); // 去掉 "Bearer "
        // 3. 去 Redis 查询 Token 是否存在
        // 这里的 key 必须和 Service 里存的时候保持一致 ("login:token:" + token)
        String redisKey = "login:token:" + token;
        String userJson = stringRedisTemplate.opsForValue().get(redisKey);

        // 4. Redis 里没有数据 (说明 Token 过期了，或者根本就是假的)
        if (StrUtil.isBlank(userJson)) {
            returnUnauthorized(response, "Token 已过期或无效，请重新登录");
            return false;
        }

        // 5. 还原 User 对象 (Hutool)
        User user = JSONUtil.toBean(userJson, User.class);

        // 6. 构造 LoginUserDTO 并存入 ThreadLocal
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setId(user.getId());
        loginUserDTO.setUserAccount(user.getUserAccount());
        loginUserDTO.setUserName(user.getUserName());
        loginUserDTO.setUserRole(user.getUserRole());
        loginUserDTO.setToken(token); // 👈 关键：把 token 也存进去了
        
        UserContext.set(loginUserDTO);

        // 7. 【重要】自动续期
        // 用户既然在操作，就说明他是活跃的，把他登录有效期再延长 1 天
        Integer ttl = securityProperties.getTokenTtlHours();
        stringRedisTemplate.expire(redisKey, ttl, TimeUnit.HOURS);

        return true; // 放行
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        // 8. 请求结束，必须清空 ThreadLocal，防止内存泄漏
        UserContext.remove();
    }

    private void returnUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=utf-8");
        // 优雅写法：利用 Hutool + 统一响应对象
        String json = JSONUtil.toJsonStr(com.acha.project.common.BaseResponse.error(401, message));
        response.getWriter().write(json);
    }
}