package com.acha.project.common;

import com.acha.project.model.entity.User;

/**
 * 用户上下文工具类 (基于 ThreadLocal)
 * 作用：在同一线程内，随时随地存取用户信息
 */
public class UserContext {

    // 这是一个线程本地变量，每个线程都有自己独立的一份，互不干扰
    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();

    /**
     * 存入用户信息
     */
    public static void set(User user) {
        USER_HOLDER.set(user);
    }

    /**
     * 获取用户信息
     */
    public static User get() {
        return USER_HOLDER.get();
    }

    /**
     * 清除用户信息 (非常重要！防止内存泄漏)
     */
    public static void remove() {
        USER_HOLDER.remove();
    }
}