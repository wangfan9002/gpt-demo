package com.gy.gpt.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "发送验证码Request")
@Data
public class SendCodeRequest {
    @Schema(description = "手机号", example = "130*****")
    private String mobile;
}
