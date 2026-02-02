package com.acha.project.common;

import lombok.Data;
import java.io.Serializable;

/**
 * 通用返回类
 * @param <T> 数据类型
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;      // 状态码 (比如 0 表示成功，40000 表示失败)
    private T data;        // 数据 (真正的返回值，比如用户ID)
    private String message;// 消息 (比如 "注册成功")

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    // 成功的静态方法，方便调用
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "操作成功");
    }

    // 成功的静态方法，带自定义消息
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(0, data, message);
    }


    // 失败的静态方法
    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }
}