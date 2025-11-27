package com.example.demo.dto.flow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateFlowDefinitionRequest {

    @NotBlank(message = "Flow name is required")
    @Size(max = 200, message = "Name max 200 characters")
    private String name;

    @NotBlank(message = "Flow definition content is required")
    private String definition;

    private Boolean isPublished;
}