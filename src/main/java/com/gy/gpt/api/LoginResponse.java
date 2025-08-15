package com.gy.gpt.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "注册响应")
@Data
public class LoginResponse {
    @Schema(description = "账号显示名称", example = "130*****")
    private String account;
    @Schema(description = "用户ID", example = "23**")
    private Long userId;
    @Schema(description = "用户token", example = "ad23**")
    private String token;
}
