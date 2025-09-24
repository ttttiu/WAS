package com.was.filter;

import com.was.pojo.JwtProperties;
import com.was.pojo.entity.LoginUser;
import com.was.pojo.entity.User;
import com.was.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT令牌认证过滤器
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 获取token
//        String token = request.getHeader(jwtProperties.getUserTokenName());
        // 从Cookie中获取token
        String token = getTokenFromCookie(request);

        if (token != null && !token.isEmpty()) {
            try {
                // 解析token
                Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
                String userId = claims.get("userId").toString();

                // 从redis中获取用户信息
                LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get("login:" + userId);

                if (loginUser != null) {
                    // 构建认证对象
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                // token解析失败处理
                log.error("token解析失败: ", e);
            }
        }

        filterChain.doFilter(request, response);
        /*// 如果没有token，直接放行
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析token获取其中的userId
        long userId;
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            userId = Long.parseLong(claims.get("userId").toString());
        } catch (Exception e) {
            log.error("token解析失败: ", e);
            filterChain.doFilter(request, response);
            return;
        }

        // 从redis中获取用户信息
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get("login:"+userId);
        if (loginUser == null) {
            log.warn("用户未登录或已过期，userId: {}", userId);
            filterChain.doFilter(request, response);
            return;
        }

        // 存入SecurityContextHolder
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 继续执行过滤器链
        filterChain.doFilter(request, response);*/


    }
    /**
     * 从Cookie中获取token
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (jwtProperties.getUserTokenName().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
