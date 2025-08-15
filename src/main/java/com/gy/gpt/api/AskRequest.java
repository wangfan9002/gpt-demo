package com.gy.gpt.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AskRequest {
    @Schema(description = "文件名", example = "250815-2b0371***5688350e576bc6.txt")
    private String fileName;
    @Schema(description = "具体内容", example = "人员的一些信息")
    private String content;
}
