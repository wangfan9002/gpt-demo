package com.gy.gpt.exception;

import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
public enum ErrorCode {
    // 通用错误码
    SUCCESS(0, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    LIMIT(429, "操作过于频繁，请稍后再试"),

    // 业务错误码 1000-1999
    USER_NOT_LOGIN(1000, "用户未登录或登录已过期"),
    UNKNOW_ERROR(1001, "未知错误"),
    VALID_CODE_NOT_EMPTY(1002, "验证码不能为空"),

    MOBILE_NOT_EMPTY(1011, "手机号不能为空"),
    VALID_CODE_ERROR(1012, "验证码错误或已过期"),
    UNIT_NAME_NOT_EMPTY(1013, "单位名称不能为空"),
    USER_EXISTS(1022, "用户已存在"),
    USER_PASSWORD_ERROR(1023, "密码错误"),
    USER_LOCKED(1024, "用户已被锁定");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}