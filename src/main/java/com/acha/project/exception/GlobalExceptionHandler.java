package com.acha.project.exception;

import com.acha.project.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 作用：捕获代码里抛出的异常，统一转成 JSON 返回给前端
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. 捕获我们自己抛出的 RuntimeException (比如：账号已存在)
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e); // 打印错误日志到控制台
        return BaseResponse.error(500, e.getMessage());
    }

    // 2. 捕获 Spring Validation 参数校验异常 (比如：密码太短)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> validationExceptionHandler(MethodArgumentNotValidException e) {
        String defaultMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("validationException", e);
        return BaseResponse.error(400, defaultMessage);
    }

    // 3. 捕获所有漏网之鱼
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> exceptionHandler(Exception e) {
        log.error("Exception", e);
        return BaseResponse.error(500, "系统内部异常");
    }
}