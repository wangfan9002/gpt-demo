package com.gy.gpt.util;

public class LoginContext {
    /**
     * 当前登录用户的  threadLocal
     */
    private static final ThreadLocal<Long> LOGIN_USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置登录信息到 threadLocal
     *
     * @param userId
     */
    public static void setLoginInfo(Long userId) {
        LOGIN_USER_HOLDER.set(userId);
    }

    /**
     * 获取当前登录人 uid
     *
     * @return
     */
    public static Long getLoginUser() {
        return LOGIN_USER_HOLDER.get();
    }


}
