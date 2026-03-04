package com.was.config;

import com.was.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 安全配置
 * 
 * 功能：
 * 1. 认证（Authentication）- JWT Token 认证
 * 2. 授权（Authorization）- 基于权限的访问控制
 * 3. 安全（Security）- 密码加密、会话管理等
 */
@Configuration
@EnableMethodSecurity  // 启用方法级安全注解
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
     * CORS 配置
     * 允许跨域请求
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));  // 允许所有来源
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);  // 允许携带凭证
        configuration.setMaxAge(3600L);  // 预检请求缓存时间
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
                // 配置 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 配置授权规则
                .authorizeHttpRequests(authorize -> authorize
                        // 认证相关接口 - 允许所有人访问（包括已登录和未登录）
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/auth/logout").permitAll()
                        
                        // 公开访问的接口 - 所有人都可以访问（包括已登录和未登录）
                        .requestMatchers("/user/home").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/blog/page").permitAll()  // 博客列表
                        .requestMatchers("/blog/{id}").permitAll()  // 博客详情
                        .requestMatchers("/category/list").permitAll()  // 分类列表
                        .requestMatchers("/tag/list").permitAll()  // 标签列表
                        
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                
                // 禁用CSRF保护（因为使用JWT进行认证，是无状态的）
                .csrf(AbstractHttpConfigurer::disable)
                
                // 配置会话管理为无状态
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // 配置异常处理
                .exceptionHandling(exception -> exception
                        // 未认证时的处理
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":0,\"msg\":\"未认证，请先登录\"}");
                        })
                        // 权限不足时的处理
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":0,\"msg\":\"权限不足\"}");
                        })
                );
                
        // 将JWT过滤器添加到UsernamePasswordAuthenticationFilter之前
        http.addFilterBefore(jwtTokenAdminInterceptor, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
