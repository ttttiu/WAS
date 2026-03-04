package com.was.aspect;

import com.was.annotation.RequirePermission;
import com.was.pojo.entity.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 权限验证切面
 */
@Aspect
@Component
@Slf4j
public class PermissionAspect {

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("用户未认证，拒绝访问");
            throw new RuntimeException("用户未认证");
        }

        // 获取用户权限列表
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String requiredPermission = requirePermission.value();

        // 检查是否拥有所需权限
        boolean hasPermission = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(requiredPermission));

        if (!hasPermission) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            log.warn("用户 {} 缺少权限: {}", loginUser.getUsername(), requiredPermission);
            throw new RuntimeException("权限不足，需要权限: " + requiredPermission);
        }

        log.debug("用户拥有权限: {}", requiredPermission);
        return joinPoint.proceed();
    }
}
