package com.was.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtProperties {

    // 用户密钥
    private String userSecretKey = "this_is_user_secret_key_at_least_32_bytes";
    private long userTtl = 3600000; // 管理员token有效期1小时
    private String userTokenName = "token";
}
