package com.gy.gpt.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Schema(description = "注册实体")
@Data
public class LoginRequest {
    @Schema(description = "手机号", example = "130*****")
    private String mobile;
    @Schema(description = "验证码", example = "23**")
    private String validCode;
    @Schema(description = "单位名称", example = "顺丰科技")
    private String unitName;
}
