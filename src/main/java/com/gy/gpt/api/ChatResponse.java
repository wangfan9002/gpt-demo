package com.gy.gpt.api;

import lombok.Data;

@Data
public class ChatResponse {
    private int code;
    private String msg;
    private String data;
}
