package com.example.demo.util;

import com.example.demo.entity.User;
import com.example.demo.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 安全性工具類：用於在業務層快速獲取當前登入使用者資訊
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    /**
     * 從 SecurityContext 中獲取當前使用者的 UUID
     * @return 當前使用者的 UUID
     * @throws RuntimeException 如果用戶未登入或找不到對應的 User
     */
    public UUID getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Jwt jwt) {
            // 從 JWT 的 Subject (通常是 username) 獲取
            String username = jwt.getSubject();
            return userRepository.findByUsername(username)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));
        } else {
            throw new RuntimeException("User not authenticated or principal type is unexpected.");
        }
    }

    /**
     * 獲取當前登入使用者的 User Entity
     * @return 當前使用者的 User Entity
     */
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Jwt jwt) {
            String username = jwt.getSubject();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));
        } else {
            throw new RuntimeException("User not authenticated or principal type is unexpected.");
        }
    }
}