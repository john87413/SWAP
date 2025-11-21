package com.example.demo.dto.user;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    private String email;
    private String displayName;
    private Boolean isActive;
    private List<Integer> roleIds;
}
