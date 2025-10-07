package com.was.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    /**
     * 注册接口限流器
     * 每半个小时5个请求
     * @return RateLimiter实例
     */
    @Bean
    public RateLimiter registerRateLimiter() {
        // 每半小时5个请求
        return RateLimiter.create(5.0 / 1800.0);
    }
}
