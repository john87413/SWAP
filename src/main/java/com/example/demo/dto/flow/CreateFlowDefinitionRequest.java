package com.example.demo.dto.flow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateFlowDefinitionRequest {

    @NotBlank(message = "Flow name is required")
    @Size(max = 200, message = "Name max 200 characters")
    private String name;

    @NotBlank(message = "Flow key is required")
    @Size(max = 100, message = "Key max 100 characters")
    private String key;

    @NotBlank(message = "Flow definition content is required")
    private String definition;

    @NotNull(message = "Version must be set")
    private Integer version;

    private Boolean isPublished;
}