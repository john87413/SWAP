package com.example.demo.util;

import com.example.demo.dto.user.UserDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;

import java.util.stream.Collectors;

/**
 * 用戶 DTO 轉換器 - 負責將 User Entity 轉換為 User DTO
 */
public class UserMapper {

    private UserMapper() {
        // 隱藏公共建構函式，這是一個靜態工具類
    }

    /**
     * 將 User Entity 轉換為 UserDto
     */
    public static UserDto mapToDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .isActive(user.getIsActive())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .build();
    }
}