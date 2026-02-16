package com.acha.project.aspectj;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.acha.project.common.UserContext;
import com.acha.project.model.dto.user.LoginUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * 请求日志切面
 * 作用：拦截所有 Controller 的请求，打印请求参数、返回值、耗时等信息
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * 定义切点：所有 com.acha.project.controller 包下的所有方法
     */
    @Pointcut("execution(* com.acha.project.controller.*.*(..))")
    public void controllerPointcut() {}

    /**
     * 环绕通知：在方法执行前后都做事情
     */
    @Around("controllerPointcut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        // 1. 开始计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 2. 获取请求相关信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // 生成唯一的请求 ID，方便日志链路追踪
        String requestId = UUID.randomUUID().toString();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();

        // 获取当前操作人（如果有）
        String operator = "Guest";
        try {
            LoginUserDTO loginUser = UserContext.get();
            if (loginUser != null) {
                operator = loginUser.getUserName() + "(" + loginUser.getId() + ")";
            }
        } catch (Exception ignored) {}

        // 3. 打印请求日志
        log.info("Request Start: [{}] {} {}, Operator: {}, Args: {}", 
            requestId, method, url, operator, point.getArgs());

        // 4. 执行原方法
        Object result = point.proceed();

        // 5. 结束计时
        stopWatch.stop();

        // 6. 打印响应日志
        log.info("Request End: [{}] Time: {}ms, Response: {}", 
            requestId, stopWatch.getTotalTimeMillis(), JSONUtil.toJsonStr(result));
            
        return result;
    }
}
