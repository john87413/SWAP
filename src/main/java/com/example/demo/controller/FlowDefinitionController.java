package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.flow.CreateFlowDefinitionRequest;
import com.example.demo.dto.flow.FlowDefinitionDto;
import com.example.demo.dto.flow.FlowDefinitionListDto;
import com.example.demo.dto.flow.UpdateFlowDefinitionRequest;
import com.example.demo.service.FlowDefinitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/flow-definitions")
@RequiredArgsConstructor
public class FlowDefinitionController {

    private final FlowDefinitionService flowDefinitionService;

    /**
     * 獲取所有流程定義 (分頁)
     */
    @GetMapping
    public PageResponse<FlowDefinitionListDto> getAllFlowDefinitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 排序欄位需在 FlowDefinition Entity 中存在，以便 Spring Data JPA 處理
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 呼叫優化後的方法
        Page<FlowDefinitionListDto> result = flowDefinitionService.getAllFlowDefinitions(pageable);

        return PageResponse.of(result);
    }

    /**
     * 獲取單個流程定義
     */
    @GetMapping("/{id}")
    public FlowDefinitionDto getFlowDefinition(@PathVariable UUID id) {
        return flowDefinitionService.getFlowDefinitionById(id);
    }

    /**
     * 創建新的流程定義
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createFlowDefinition(@Valid @RequestBody CreateFlowDefinitionRequest request) {
        FlowDefinitionDto dto = flowDefinitionService.createFlowDefinition(request);
        return ResponseEntity.ok(
                ApiResponse.success("Flow definition created successfully", dto)
        );
    }

    /**
     * 更新流程定義
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateFlowDefinition(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFlowDefinitionRequest request
    ) {
        FlowDefinitionDto dto = flowDefinitionService.updateFlowDefinition(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Flow definition updated successfully", dto)
        );
    }

    /**
     * 刪除流程定義
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteFlowDefinition(@PathVariable UUID id) {
        flowDefinitionService.deleteFlowDefinition(id);
        return ResponseEntity.ok(
                ApiResponse.success("Flow definition deleted successfully")
        );
    }
}