package com.was.config;

import com.was.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig{

    @Autowired
    private JwtAuthenticationTokenFilter jwtTokenAdminInterceptor;
    /**
     * 密码编码器Bean
     * 使用BCrypt算法对密码进行加密和匹配
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器Bean
     * 用于处理用户认证逻辑的核心组件
     * @param authConfig 认证配置对象
     * @return AuthenticationManager实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 安全过滤器链Bean
     * 配置HTTP请求的安全策略和访问控制规则
     * @param http HttpSecurity对象
     * @return SecurityFilterChain实例
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/home").permitAll()// 允许home接口访问
                        .requestMatchers("/auth/login").anonymous()  // 只允许匿名用户（未登录）访问，已登录用户无法访问登录页面
                        .requestMatchers("/auth/register").anonymous()// 只允许匿名用户（未登录）访问，已登录用户无法访问注册页面
                        .requestMatchers("/error").permitAll()  // 允许错误页面
                        .anyRequest().authenticated() // 其他请求都需要认证
                )
                .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护, 因为使用JWT进行认证
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 禁用会话创建, 因为使用JWT进行会话管理
                );
        // 将JWT过滤器添加到UsernamePasswordAuthenticationFilter之前
        http.addFilterBefore(jwtTokenAdminInterceptor, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
