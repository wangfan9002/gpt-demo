package com.gy.gpt.service;

public interface VerificationCodeService {
    /**
     * 发送验证码
     * @param target 手机号/邮箱
     * @param type 验证码类型(REGISTER, LOGIN, RESET_PWD等)
     * @return 发送结果
     */
    boolean sendCode(String target, String type);

    /**
     * 验证验证码
     * @param target 手机号/邮箱
     * @param code 验证码
     * @param type 验证码类型
     * @return 是否验证通过
     */
    boolean verifyCode(String target, String code, String type);
}
