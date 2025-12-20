package com.peanut.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    // 密钥（生产环境需配置在 application.yml，至少 256 位）
    @Value("${jwt.access.secret:peanut-secret-key-32bytes-1234567890abcdef}")
    private String secret;

    // Token 过期时间（1 小时，单位：毫秒）
    @Value("${jwt.access.expiration:3600000}")
    private long expiration;

    @Value("${jwt.refresh.secret:peanut-refresh-secret-32bytes-87654321}")
    private String refreshSecret; // Refresh Token 密钥（必须和 Access Token 不同）
    @Value("${jwt.refresh.expiration:604800000}") // 7天（604800000 毫秒）
    private long refreshExpiration;

    // 生成签名密钥（JWT 必须用密钥签名，防止篡改）
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private SecretKey getRefreshSecretKey() {
        return Keys.hmacShaKeyFor(refreshSecret.getBytes());
    }

    /**
     * 1. 生成 Token（登录/注册成功后调用）
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Token 主题：存储用户名（也可存用户 ID）
                .claim("roles", userDetails.getAuthorities()) // 附加角色/权限信息
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 过期时间
                .signWith(getSecretKey()) // 用密钥签名
                .compact();
    }

    /**
     * 2. 验证 Token 有效性（核心：签名是否正确 + 是否过期 + 用户名匹配）
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        // 校验逻辑：用户名一致 + Token 未过期
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 3. 从 Token 中提取用户名
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * 4. 从 Token 中提取所有负载信息（Claims = JWT 存储用户信息的部分）
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey()) // 用密钥解析（防止 Token 被篡改）
                .build()
                .parseClaimsJws(token) // 解析 Token（无效则抛出异常）
                .getBody();
    }

    /**
     * 5. 检查 Token 是否过期
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // 存储用户名（和 Access Token 一致）
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getRefreshSecretKey()) // 用独立密钥签名
                .compact();
    }

    /** 验证 Refresh Token 有效性（仅用于刷新接口） */
    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        String username = extractRefreshUsername(token);
        return username.equals(userDetails.getUsername()) && !isRefreshTokenExpired(token);
    }

    /** 从 Refresh Token 提取用户名 */
    public String extractRefreshUsername(String token) {
        return extractRefreshClaims(token).getSubject();
    }

    /** 检查 Refresh Token 是否过期 */
    public boolean isRefreshTokenExpired(String token) {
        return extractRefreshClaims(token).getExpiration().before(new Date());
    }

    private Claims extractRefreshClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getRefreshSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getAccessExpiration() {
        return expiration; // 暴露 accessExpiration 给外部
    }
}