package com.was.aspect;

import com.google.common.util.concurrent.RateLimiter;
import com.was.annotation.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class RateLimiterAspect {

    @Autowired
    private RateLimiter registerRateLimiter;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        String clientIp = getClientIp(request);

        RateLimiter limiter = null;
        if (uri.contains("/auth/register")) {
            limiter = registerRateLimiter;
        }

        if (limiter != null && !limiter.tryAcquire()) {
            log.warn("请求过于频繁，IP地址: {}, URI: {}", clientIp, uri);
            throw new RuntimeException("注册过于频繁，请稍后再试");
        }

        return joinPoint.proceed();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
