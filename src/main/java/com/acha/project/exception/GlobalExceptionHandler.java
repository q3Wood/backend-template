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
    // 业务异常是预期的逻辑分支 (如：密码错误)，不用打 ERROR 日志，以免报警系统误报
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.warn("BusinessException: code={}, message={}", e.getCode(), e.getMessage());
        return BaseResponse.error(e.getCode(), e.getMessage());
    }

    // 1. 捕获 RuntimeException (运行时异常)
    // 只要是未捕获的运行时异常，都算系统故障，必须打 ERROR 日志并通知开发人员
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException: ", e); 
        return BaseResponse.error(500, "系统开小差了，请稍后再试");
    }

    // 2. 捕获 Spring Validation 参数校验异常 (比如：密码太短)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> validationExceptionHandler(MethodArgumentNotValidException e) {
        String defaultMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("validationException", e);
        return BaseResponse.error(400, defaultMessage);
    }

    // 3. 捕获所有漏网之鱼 (Checked Exception + 其他所有异常)
    // 比如：文件读写IO异常、数据库连接异常等
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> exceptionHandler(Exception e) {
        log.error("UnknownException: ", e);
        return BaseResponse.error(500, "系统开小差了，请稍后再试");
    }
}