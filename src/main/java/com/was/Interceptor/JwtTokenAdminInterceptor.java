package com.was.Interceptor;


import com.was.pojo.JwtProperties;
import com.was.pojo.entity.LoginUser;
import com.was.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 校验jwt
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler HandlerMethod
     * @return  boolean
     * @throws Exception 抛出
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //获取token
        String token = request.getHeader(jwtProperties.getUserTokenName());
        if (token == null) {
            //没有token，直接放行
            return true;
        }
        //解析token获取其中的userid
        long userId;
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            userId = Long.parseLong(claims.get("userId").toString());
        } catch (Exception e) {
            throw new RuntimeException("token非法");
        }
        //从redis中获取用户信息
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(userId);
        if (loginUser == null) {
            throw new RuntimeException("用户未登录");
        }
        //存入SecurityContextHolder
        //TODO:获取权限信息封装到AuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        return true;
        }
}
