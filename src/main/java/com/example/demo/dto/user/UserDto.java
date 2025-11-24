package com.example.demo.dto.user;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String displayName;
    private Boolean isActive;
    private List<String> roles;

    public UserDto(UUID id, String username, String email, String displayName, Boolean isActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.isActive = isActive;
        // 必須確保 roles 列表被初始化
        this.roles = new ArrayList<>();
    }
}
