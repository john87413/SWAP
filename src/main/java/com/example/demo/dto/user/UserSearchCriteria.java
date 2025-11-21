package com.example.demo.dto.user;

import lombok.*;

@Getter
@Setter
public class UserSearchCriteria {
    private String keyword;
    private Boolean isActive;
    private String roleName;
}