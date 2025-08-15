package com.gy.gpt.interceptor;


import com.gy.gpt.util.TraceIdUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 尝试从请求头中获取traceId
        String traceId = request.getHeader(TraceIdUtil.TRACE_ID);

        // 如果没有则生成新的traceId
        if (traceId == null || traceId.isEmpty()) {
            traceId = TraceIdUtil.generateTraceId();
        }

        // 设置traceId到MDC
        TraceIdUtil.setTraceId(traceId);

        // 将traceId添加到响应头中，方便前端获取
        response.addHeader(TraceIdUtil.TRACE_ID, traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 请求完成后清除traceId
        TraceIdUtil.clearTraceId();
    }
}