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

    // 0. 捕获业务异常 (自定义异常)
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return BaseResponse.error(e.getCode(), e.getMessage());
    }

    // 1. 捕获 RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e); // 打印错误日志到控制台
        String message = e.getMessage();
        // 如果当前异常信息为空，尝试获取 Cause 的信息，或者给默认值
        if (message == null || message.trim().isEmpty()) {
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            } else {
                message = "系统运行时异常";
            }
        }
        return BaseResponse.error(500, message);
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