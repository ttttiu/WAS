package com.was.controller;

import com.was.pojo.JwtProperties;
import com.was.pojo.Result;
import com.was.pojo.dto.LoginDTO;
import com.was.pojo.dto.RegisterDTO;
import com.was.pojo.vo.LoginVO;
import com.was.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     *  注册
     * @param registerDTO 注册参数
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO, BindingResult bindingResult) {
        log.info("用户注册: {}", registerDTO);
        // 检查是否有验证错误
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return Result.error(errorMessage);
        }
        authService.register(registerDTO);
        return Result.success();
    }

    /**
     *  登录
     * @param loginDTO 登录参数
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response){
        log.info("用户登录: {}", loginDTO);
        return authService.login(loginDTO, response);

//        //登录成功后，生成jwt令牌
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", user.getId());
//        String token = JwtUtil.createJWT(
//                jwtProperties.getUserSecretKey(),
//                jwtProperties.getUserTtl(),
//                claims);
//
//        //将jwt令牌写入cookie
//        ResponseCookie cookie = ResponseCookie.from(jwtProperties.getUserTokenName(), token)
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(Duration.ofMillis(jwtProperties.getUserTtl()))
//                .sameSite("Strict")
//                .build();
//        response.setHeader("Set-Cookie", cookie.toString());
        //返回登录结果
//        return Result.success(AdminLoginVO.builder()
//                .id(user.getId())
//                .userName(user.getUserName())
//                .build());
    }


    /**
     *  登出
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletResponse response) {
        log.info("用户登出");
        ResponseCookie cookie = ResponseCookie.from(jwtProperties.getUserTokenName(), "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());


        return authService.logout();
    }
}
