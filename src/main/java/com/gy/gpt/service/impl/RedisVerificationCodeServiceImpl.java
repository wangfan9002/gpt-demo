package com.gy.gpt.service.impl;
import com.gy.gpt.exception.BusinessException;
import com.gy.gpt.exception.ErrorCode;
import com.gy.gpt.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//@Service
@Slf4j
@RequiredArgsConstructor
public class RedisVerificationCodeServiceImpl implements VerificationCodeService {

    private final StringRedisTemplate redisTemplate;

    // 验证码有效期(5分钟)
    private static final long CODE_EXPIRE = 5 * 60;
    // 验证码发送间隔(60秒)
    private static final long CODE_SEND_INTERVAL = 60;
    // 每日发送上限
    private static final int DAILY_SEND_LIMIT = 10;

    @Override
    public boolean sendCode(String target, String type) {
        // 1. 参数校验
        if (!isValidTarget(target)) {
            throw new IllegalArgumentException("无效的手机号/邮箱");
        }

        // 2. 限流检查
        checkSendFrequency(target, type);

        // 3. 生成验证码(6位数字)
        String code = generateRandomCode();

        // 4. 存储验证码到Redis
        String redisKey = buildRedisKey(target, type);
        redisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE, TimeUnit.SECONDS);

        // 5. 记录发送次数(用于限流)
        recordSendCount(target, type);

        // 6. 实际发送验证码(模拟实现)
        return doSendVerificationCode(target, code);
    }

    @Override
    public boolean verifyCode(String target, String code, String type) {
        String redisKey = buildRedisKey(target, type);
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            return false;
        }

        if (storedCode.equals(code)) {
            // 验证成功后删除验证码(防止重复使用)
            redisTemplate.delete(redisKey);
            return true;
        }

        return false;
    }

    private String generateRandomCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private String buildRedisKey(String target, String type) {
        return String.format("verification:code:%s:%s", type, target);
    }

    private void checkSendFrequency(String target, String type) {
        // 检查发送间隔
        String lastSendKey = buildLastSendKey(target, type);
        Long lastSendTime = redisTemplate.getExpire(lastSendKey);
        if (lastSendTime != null && lastSendTime > 0) {
            throw new BusinessException(ErrorCode.LIMIT);
        }

        // 检查当日发送次数
        String countKey = buildDailyCountKey(target, type);
        Integer sendCount = redisTemplate.opsForValue().get(countKey) != null ?
                Integer.parseInt(redisTemplate.opsForValue().get(countKey)) : 0;

        if (sendCount >= DAILY_SEND_LIMIT) {
            throw new BusinessException(ErrorCode.LIMIT.getCode(), "今日验证码发送次数已达上限");
        }
    }

    private void recordSendCount(String target, String type) {
        // 记录最后一次发送时间
        String lastSendKey = buildLastSendKey(target, type);
        redisTemplate.opsForValue().set(lastSendKey, "1", CODE_SEND_INTERVAL, TimeUnit.SECONDS);

        // 记录当日发送次数
        String countKey = buildDailyCountKey(target, type);
        redisTemplate.opsForValue().increment(countKey);

        // 设置当日发送次数key的过期时间(到当天23:59:59)
        LocalDateTime midnight = LocalDateTime.now().with(LocalTime.MAX);
        long secondsUntilMidnight = ChronoUnit.SECONDS.between(LocalDateTime.now(), midnight);
        redisTemplate.expire(countKey, secondsUntilMidnight, TimeUnit.SECONDS);
    }

    private String buildLastSendKey(String target, String type) {
        return String.format("verification:last_send:%s:%s", type, target);
    }

    private String buildDailyCountKey(String target, String type) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return String.format("verification:count:%s:%s:%s", today, type, target);
    }

    private boolean doSendVerificationCode(String target, String code) {
        // 实际发送逻辑，可以是短信或邮件
        // 这里简单打印到日志
        log.info("向 {} 发送验证码: {}", target, code);
        return true;
    }

    private boolean isValidTarget(String target) {
        // 简单的手机号/邮箱格式验证
        return target.matches("^(1[3-9]\\d{9}|\\w+@\\w+\\.\\w+)$");
    }
}