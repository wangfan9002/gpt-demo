package com.gy.gpt.exception;

import lombok.Getter;

public class BusinessException extends RuntimeException {
    /**
     * 错误编码
     */
    @Getter
    private int code;

    /**
     * 错误消息
     */
    @Getter
    private String message;
;

    /**
     * 构造方法
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法（使用枚举错误码）
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.INTERNAL_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造方法（自定义消息覆盖枚举默认消息）
     * @param errorCode 错误码枚举
     * @param message 自定义错误信息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
    }
}
