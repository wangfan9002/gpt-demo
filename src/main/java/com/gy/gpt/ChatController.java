package com.gy.gpt;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.gy.gpt.api.ChatRequest;
import com.gy.gpt.api.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.concurrent.*;

@Slf4j
@RestController
public class ChatController {
    @Resource
    private RestTemplate restTemplate;
    @Value("${answer.server:}")
    private String serverUrl;

    // 用于存储各个前端会话的emitter和状态
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> stopFlags = new ConcurrentHashMap<>();

    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 20,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue(1000),
            new ThreadFactoryBuilder().setNameFormat("rec-thread-%d").build());

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestParam String sessionId,
                                 @RequestParam String prompt) {


        SseEmitter emitter = new SseEmitter(300_000L); // 不超时
        emitters.put(sessionId, emitter);
        stopFlags.put(sessionId, false);
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setSessionId(sessionId);
        chatRequest.setPrompt(prompt);
        log.info("serverUrl:{}", serverUrl);
        // 模拟流式回复（可换成实际的LLM对话流逻辑）

        threadPool.submit(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ChatRequest> request = new HttpEntity<>(chatRequest, headers);
                ChatResponse answer = restTemplate.postForObject(serverUrl, request, ChatResponse.class);
                log.info("answer:{}", answer);
                String toPrint = (answer != null && answer.getData() != null) ? answer.getData() : "[无]";
                for (int i = 0; i < toPrint.length(); i++) {
                    if (stopFlags.getOrDefault(sessionId, false)) {
                        emitter.send(SseEmitter.event().data("[已暂停]"));
                        break;
                    }
                    emitter.send(SseEmitter.event().data(toPrint.substring(i, i + 1)));
                    Thread.sleep(80); // 逐字推送,可调快慢
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                emitters.remove(sessionId);
                stopFlags.remove(sessionId);
            }
        });
        emitter.onCompletion(() -> emitters.remove(sessionId));
        emitter.onTimeout(() -> emitters.remove(sessionId));
        emitter.onError((ex) -> emitters.remove(sessionId));

        return emitter;
    }


    @GetMapping(value = "/chat-static", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStaticStream(@RequestParam String sessionId,
                                 @RequestParam String prompt) {
        SseEmitter emitter = new SseEmitter(0L); // 不超时
        emitters.put(sessionId, emitter);
        stopFlags.put(sessionId, false);

        // 模拟流式回复（可换成实际的LLM对话流逻辑）
        threadPool.submit(() -> {
            try {
                String[] pieces = {
                        "你好，这里是 Chat。",
                        "现在我在逐步输出。",
                        "你可以随时暂停。"
                };
                for (String p : pieces) {
                    if (stopFlags.getOrDefault(sessionId, false)) {
                        emitter.send(SseEmitter.event().data("[已暂停输出]"));
                        break;
                    }
                    emitter.send(SseEmitter.event().data(p));
                    Thread.sleep(1000); // 模拟Token逐步输出
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                emitters.remove(sessionId);
                stopFlags.remove(sessionId);
            }
        });

        emitter.onCompletion(() -> emitters.remove(sessionId));
        emitter.onTimeout(() -> emitters.remove(sessionId));
        emitter.onError((ex) -> emitters.remove(sessionId));

        return emitter;
    }

    @PostMapping("/chat/stop")
    public String stopStream(@RequestParam String sessionId) {
        stopFlags.put(sessionId, true);
        emitters.remove(sessionId);
        return "ok";
    }
}
