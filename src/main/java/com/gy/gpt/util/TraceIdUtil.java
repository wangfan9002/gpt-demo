package com.gy.gpt.util;

import org.slf4j.MDC;
import java.util.UUID;

public class TraceIdUtil {
    public static final String TRACE_ID = "traceId";

    /**
     * 生成TraceID
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 设置TraceID到MDC
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }

    /**
     * 获取当前TraceID
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 清除TraceID
     */
    public static void clearTraceId() {
        MDC.remove(TRACE_ID);
    }
}