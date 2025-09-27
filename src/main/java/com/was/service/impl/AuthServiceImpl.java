package com.was.service.impl;

import com.was.mapper.AuthMapper;
import com.was.pojo.JwtProperties;
import com.was.pojo.Result;
import com.was.pojo.dto.LoginDTO;
import com.was.pojo.dto.RegisterDTO;
import com.was.pojo.entity.LoginUser;
import com.was.pojo.entity.User;
import com.was.pojo.vo.LoginVO;
import com.was.service.AuthService;
import com.was.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     *  注册
     * @param registerDTO 注册信息
     */
    @Override
    public void register(RegisterDTO registerDTO) {
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        if (authMapper.getUser(user.getUserName()) != null) {
            throw new RuntimeException("用户已存在");
        }
        String encode = passwordEncoder.encode(user.getPassword());//密码进行加密
        user.setPassword(encode);
        authMapper.insertUser(user);
    }

    /**
     *  登录
     * @param loginDTO 登录信息
     * @return 登录用户信息
     */
    @Override
    public Result<LoginVO> login(LoginDTO loginDTO, HttpServletResponse response) {

        //AuthenticationManagerauthenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUserName(), loginDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户名或密码错误");
        }
        //如果认证通过了，使用userid生成一个jwt，jwt存入Result返回
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        User user = loginUser.getUser();
        String userId = user.getId().toString();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        //将jwt令牌写入cookie
        ResponseCookie cookie = ResponseCookie.from(jwtProperties.getUserTokenName(), token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMillis(jwtProperties.getUserTtl()))
                .sameSite("Strict")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());//将cookie写入响应头
        //把完整的用户信息存入redis，userid作为key
        redisTemplate.opsForValue().set("login:"+userId, loginUser);
        return Result.success(LoginVO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .token(token)
                .build());
    }

    @Override
    public Result<Void> logout() {
        // 获取 SecurityContextHolder 中的 Authentication 对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 从 Authentication 中获取 principal（即 LoginUser 对象）
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        Long userId = loginUser.getUser().getId();

        // 删除 redis 中的用户信息
        redisTemplate.delete("login:" + userId);

        return Result.success();
    }
}
