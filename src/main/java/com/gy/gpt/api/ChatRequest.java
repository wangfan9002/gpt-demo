package com.gy.gpt.api;

import lombok.Data;

@Data
public class ChatRequest {
    private String sessionId;
    private String prompt;
}
