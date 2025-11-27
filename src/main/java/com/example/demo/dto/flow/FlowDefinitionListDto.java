package com.example.demo.dto.flow;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FlowDefinitionListDto {
    private UUID id;
    private String name;
    private String key;
    private Integer version;
    private Boolean isPublished;
    private LocalDateTime createdAt;

    private String createdByUsername;
    private String createdByDisplayName;

    public FlowDefinitionListDto(UUID id, String name, String key, Integer version, Boolean isPublished, LocalDateTime createdAt, String createdByUsername, String createdByDisplayName) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.version = version;
        this.isPublished = isPublished;
        this.createdAt = createdAt;
        this.createdByUsername = createdByUsername;
        this.createdByDisplayName = createdByDisplayName;
    }
}