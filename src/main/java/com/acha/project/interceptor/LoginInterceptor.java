package com.acha.project.interceptor;

import com.acha.project.model.entity.User;
import com.acha.project.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从 Session 获取用户信息
        Object userObj = request.getSession().getAttribute("USER_LOGIN_STATE");
        User user = (User) userObj;

        // 2. 没登录，拦住
        if (user == null) {
            response.setStatus(401);
            return false;
        }

        // 3. 登录了 -> 【关键】把用户存入 ThreadLocal
        UserContext.set(user);

        return true; // 放行
    }

    // 【新增】请求结束后的清理工作
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 4. 请求结束，必须清空 ThreadLocal，防止内存泄漏
        UserContext.remove();
    }
}