//package com.was.utils;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtBuilder;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//import java.util.Map;
//
//public class JwtUtil {
//    /**
//     * 生成jwt
//     * 使用Hs256算法, 私匙使用固定秘钥
//     *
//     * @param secretKey jwt秘钥
//     * @param ttlMillis jwt过期时间(毫秒)
//     * @param claims    设置的信息
//     * @return jwt字符串
//     */
//    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
//        // 指定签名的时候使用的签名算法，也就是header那部分
//        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//
//        // 生成JWT的时间
//        long expMillis = System.currentTimeMillis() + ttlMillis;
//        Date exp = new Date(expMillis);
//
//        // 设置jwt的body
//        JwtBuilder builder = Jwts.builder()
//                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
//                .setClaims(claims)
//                // 设置签名使用的签名算法和签名使用的秘钥
//                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
//                // 设置过期时间
//                .setExpiration(exp);
//
//        return builder.compact();
//    }
//
//    /**
//     * Token解密
//     *
//     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
//     * @param token 加密后的token
//     * @return 解密后的信息
//     */
//    public static Claims parseJWT(String secretKey, String token) {
//        // 返回DefaultJwtParser
//        return Jwts.parser()
//                // 设置签名的秘钥
//                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
//                // 设置需要解析的jwt
//                .parseClaimsJws(token).getBody();
//    }
//
//}

package com.was.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return jwt字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 生成签名密钥
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 计算过期时间
        Date exp = null;
        if (ttlMillis > 0) {
            long expMillis = System.currentTimeMillis() + ttlMillis;
            exp = new Date(expMillis);
        }

        // 构建JWT
        JwtBuilder builder = Jwts.builder();

        // 如果有自定义声明，先设置claims
        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }

        // 设置标准声明
        builder
                .subject("web-auth-system")  // 设置主题
                .issuedAt(new Date());       // 设置签发时间

        // 设置过期时间
        if (exp != null) {
            builder.expiration(exp);
        }

        // 设置签名
        builder.signWith(key, Jwts.SIG.HS256);

        return builder.compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token 加密后的token
     * @return 解密后的信息
     */
    public static Claims parseJWT(String secretKey, String token) {
        // 生成签名密钥
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 解析JWT令牌
        return Jwts.parser()
                .verifyWith(key)  // 使用新的验证API
                .build()
                .parseSignedClaims(token)  // 使用新的解析方法
                .getPayload();  // 获取载荷
    }
}

