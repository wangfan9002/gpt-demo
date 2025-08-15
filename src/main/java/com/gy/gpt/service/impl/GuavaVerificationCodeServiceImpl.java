package com.gy.gpt.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.gy.gpt.exception.BusinessException;
import com.gy.gpt.exception.ErrorCode;
import com.gy.gpt.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class GuavaVerificationCodeServiceImpl implements VerificationCodeService {

    // 验证码缓存（5分钟过期）
    private final LoadingCache<String, String> codeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    return "";
                }
            });

    // 最后发送时间缓存, 2秒间隔限制）
    private final Cache<String, Long> lastSendCache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build();

    // 每日发送计数缓存（24小时过期）
    private final LoadingCache<String, AtomicInteger> dailyCountCache = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build(new CacheLoader<String, AtomicInteger>() {
                @Override
                public AtomicInteger load(String key) throws Exception {
                    return new AtomicInteger(0);
                }
            });

    // 每日发送上限
    private static final int DAILY_SEND_LIMIT = 500;

    @Override
    public boolean sendCode(String target, String type) {
        // 1. 参数校验
        if (!isValidTarget(target)) {
            throw new IllegalArgumentException("无效的手机号");
        }

        // 2. 限流检查
        checkSendFrequency(target, type);

        // 3. 生成验证码(6位数字)
        String code = generateRandomCode();

        // 4. 存储验证码
        String cacheKey = buildCacheKey(target, type);
        codeCache.put(cacheKey, code);

        // 5. 记录发送次数
        recordSendCount(target, type);

        // 6. 实际发送验证码(模拟实现)
        return doSendVerificationCode(target, code);
    }

    @Override
    public boolean verifyCode(String target, String code, String type) {
        String cacheKey = buildCacheKey(target, type);
        try {
            String storedCode = codeCache.getIfPresent(cacheKey);
            if (storedCode == null || !storedCode.equals(code)) {
                return false;
            }

            // 验证成功后删除验证码
            codeCache.invalidate(cacheKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void checkSendFrequency(String target, String type) {
        String frequencyKey = buildFrequencyKey(target, type);

        // 检查发送间隔
        if (lastSendCache.getIfPresent(frequencyKey) != null) {
            log.info("发送间隔太短，失败");
            throw new BusinessException(ErrorCode.LIMIT);
        }

        // 检查当日发送次数
        String countKey = buildDailyCountKey(target, type);
        int sendCount = dailyCountCache.getUnchecked(countKey).get();

        if (sendCount >= DAILY_SEND_LIMIT) {
            throw new BusinessException(429, "今日验证码发送次数已达上限");
        }
    }

    private void recordSendCount(String target, String type) {
        // 记录最后一次发送时间
        String frequencyKey = buildFrequencyKey(target, type);
        lastSendCache.put(frequencyKey, System.currentTimeMillis());

        // 增加当日发送计数
        String countKey = buildDailyCountKey(target, type);
        dailyCountCache.getUnchecked(countKey).incrementAndGet();
    }

    private String buildCacheKey(String target, String type) {
        return String.format("code:%s:%s", type, target);
    }

    private String buildFrequencyKey(String target, String type) {
        return String.format("freq:%s:%s", type, target);
    }

    private String buildDailyCountKey(String target, String type) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return String.format("count:%s:%s:%s", today, type, target);
    }

    private String generateRandomCode() {
        return "1234";
        // return String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000));
    }

    private boolean doSendVerificationCode(String target, String code) {
        // 模拟发送逻辑
        log.info("向 {} 发送验证码: {}", target, code);
        return true;
    }

    private boolean isValidTarget(String target) {
        return target.matches("^(1[3-9]\\d{9}|\\w+@\\w+\\.\\w+)$");
    }
}
