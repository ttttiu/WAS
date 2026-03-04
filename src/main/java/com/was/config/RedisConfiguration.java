package com.was.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
public class RedisConfiguration {

    /**
     * 自定义redisTemplate配置
     * 使用Jackson序列化方式，避免Fastjson的autoType安全问题
     *
     * @param redisConnectionFactory redis连接工厂
     * @return redisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        
        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        
        ObjectMapper objectMapper = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会抛出异常
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL
        );
        // 注册JavaTimeModule以支持Java 8日期时间类型（LocalDateTime等）
        objectMapper.registerModule(new JavaTimeModule());
        
        // 添加 SimpleGrantedAuthority 的 Mixin，解决反序列化问题
        objectMapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class);
        
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        
        // key使用StringRedisSerializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        
        // key采用String的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    
    /**
     * SimpleGrantedAuthority 的 Mixin 类
     * 用于告诉 Jackson 如何反序列化 SimpleGrantedAuthority
     */
    abstract static class SimpleGrantedAuthorityMixin {
        @JsonCreator
        public SimpleGrantedAuthorityMixin(@JsonProperty("authority") String authority) {
        }
    }

}
