package com.example.demo.dto.flow;

import com.example.demo.dto.user.UserDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FlowDefinitionDto {
    private UUID id;
    private String name;
    private String key;
    private Integer version;
    private String definition; // 流程圖的 JSON/XML
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private UserDto createdBy;
}