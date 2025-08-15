package com.gy.gpt.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.gpt.common.Result;
import com.gy.gpt.exception.ErrorCode;
import com.gy.gpt.util.JwtUtil;
import com.gy.gpt.util.LoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        /**
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, utoken"); // 根据需要添加允许的请求头
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            return true;
        }**/

        // 从Cookie中获取Token
        String token = getTokenFromCookie(request);
        if (token != null) {
            String userId = jwtUtil.validateTokenAndGetUsername(token);
            if (userId != null && !jwtUtil.isTokenExpired(token)) {
                // 将用户ID存入请求属性，供后续使用
                LoginContext.setLoginInfo(Long.parseLong(userId));
                return true;
            }
        }

        // 返回JSON格式的错误响应
        Result<?> result = Result.error(ErrorCode.USER_NOT_LOGIN);
        sendJsonResponse(response,
                HttpServletResponse.SC_UNAUTHORIZED,
                result);


        // 检查是否为AJAX请求
        if (isAjaxRequest(request)) {

            // 普通请求返回错误页面, 未通过验证，返回401未授权
           // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户未登录或登录已过期");
        }
        return false;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (this.jwtUtil.getCookieName().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) ||
                request.getHeader("Accept") != null &&
                        request.getHeader("Accept").contains("application/json");
    }

    public static void sendJsonResponse(HttpServletResponse response,
                                        int status,
                                        Result<?> result) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().write(json);
    }
}