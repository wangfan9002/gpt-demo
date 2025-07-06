package com.gy.gpt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gy.gpt.controller.ChatController;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    @Resource
    private RestTemplate restTemplate;
    @Value("${modelUrl:}")
    private String serverUrl;

    private int sessionChatCount = 6;
    // key sessionId, value:多次会话的问题及答案
    private final ConcurrentHashMap<String, List<ChatSession>> sessionChatContextMap = new ConcurrentHashMap<>();

    public String chat(String sessionId, String prompt) {
        List<ChatSession> chatList = sessionChatContextMap.get(sessionId);
        if (chatList == null) {
            chatList = new ArrayList<>();
        }
        log.info("chatList:{}", chatList);

        // todo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> dataMap = new HashMap();
        // Map<String, Object> inferMap = new HashMap();
        Map<String, String> msgMap = new HashMap();
        msgMap.put("role", "user");
        msgMap.put("content", prompt);
        dataMap.put("model", "Baichuan-M1-14B-Instruct");
        dataMap.put("messages", Arrays.asList(msgMap));
        dataMap.put("temperature", 0);
        log.info("request:{}", JSON.toJSON(dataMap));
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(dataMap, headers);

        String answer = restTemplate.postForObject(serverUrl, request, String.class);
        log.info("answer:{}", answer);
        // 1. 解析为 JSONObject
        // {"model":"Baichuan-M1-14B-Instruct","choices":[{"index":0,"message":{"role":"assistant","content":"I'm Assistant. How can I assist you today?","tool_calls":null},"finish_reason":"stop","logprobs":null}],"usage":{"prompt_tokens":14,"completion_tokens":13,"total_tokens":27}
        // ,"id":"chatcmpl-4b8b65409730468ab9af257a7f93143f","object":"chat.completion","created":1751210854
        JSONObject jsonObject = JSON.parseObject(answer);
        JSONArray choices = jsonObject.getJSONArray("choices");
        log.info("choices:{}", choices);
        JSONObject oneChoice = choices.getJSONObject(0);
        log.info("oneChoice:{}", oneChoice);
        JSONObject jsonMessage = oneChoice.getJSONObject("message");
        log.info("jsonMessage:{}", jsonMessage);
        String response = jsonMessage.getString("content");

        chatList.add(0, new ChatSession(prompt, response));
        if (chatList.size() > sessionChatCount) {
            chatList = chatList.subList(0, sessionChatCount);
        }
        sessionChatContextMap.put(sessionId, chatList);

        return response;
    }

    @Data
    @AllArgsConstructor
    public static class ChatSession {
        private String question;
        private String aiAnswer;
    }
}
