package com.gy.gpt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.gy.gpt.po.User;
import com.gy.gpt.service.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
@Slf4j
@Component
public class JwtUtil {
    @Getter
    @Value("${jwt.secret:12234}")
    private String secret;

    @Getter
    @Value("${jwt.cookie-name:utoken}")
    private String cookieName;

    @Getter
    @Value("${jwt.cookie-domain:}")
    private String cookieDomain;

    @Getter
    @Value("${jwt.cookie-secure:true}")
    private boolean cookieSecure;

    @Getter
    @Value("${jwt.expiration: 886400}")
    private Long expiration;

    @Resource
    private UserService userService;

    private static final String mobile = "mobile";

    // 生成Token
    public String generateToken(String username, String tokenSecret) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration * 1000))
                .withClaim(mobile, username)
                .sign(Algorithm.HMAC256(secret + tokenSecret));
    }

    // 验证Token并返回用户名
    public String validateTokenAndGetUsername(String token) {
        String userMobile = null;
        try {
            DecodedJWT jwt = JWT.decode(token);
            userMobile = jwt.getClaims().get(mobile).asString();
            User user = this.userService.lambdaQuery().eq(User::getMobile, userMobile).one();
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret + user.getTokenSecret()))
                    .build();
            verifier.verify(token);
            return user.getId() + "";
        } catch (JWTVerificationException e) {
            log.info("userMobile:{}, token:{}, 验证失败", userMobile, token);
            return null;
        }
    }

    // 检查Token是否过期
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
