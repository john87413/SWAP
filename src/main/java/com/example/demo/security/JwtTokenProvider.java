package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * JWT Token 生成器 - 使用 Spring Security OAuth2 Resource Server with HMAC
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration}")  // 默認 24 小時
    private long jwtExpirationMs;

    /**
     * 獲取過期時間（毫秒）
     * 可用於前端設置 token 刷新時機
     */
    public long getExpirationMs() {
        return jwtExpirationMs / 1000;
    }

    /**
     * 生成 JWT Token
     *
     * @param authentication Spring Security 認證對象
     * @return JWT Token 字符串
     */
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        // 從 Authentication 中提取角色權限
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")  // 發行者
                .issuedAt(now)   // 發行時間
                .expiresAt(now.plus(getExpirationMs(), ChronoUnit.SECONDS))  // 從配置文件讀取過期時間
                .subject(authentication.getName())  // 主體：用戶名
                .claim("roles", roles)  // 自定義聲明：用戶角色權限
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}