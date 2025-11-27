package com.example.demo.service;

import com.example.demo.dto.flow.CreateFlowDefinitionRequest;
import com.example.demo.dto.flow.FlowDefinitionDto;
import com.example.demo.dto.flow.FlowDefinitionListDto;
import com.example.demo.dto.flow.UpdateFlowDefinitionRequest;
import com.example.demo.entity.FlowDefinition;
import com.example.demo.entity.User;
import com.example.demo.repository.FlowDefinitionRepository;
import com.example.demo.util.SecurityUtils;
import com.example.demo.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlowDefinitionService {
    private final FlowDefinitionRepository flowDefinitionRepository;
    private final SecurityUtils securityUtils;
    private final UserService userService; // 依賴 UserService 轉換 UserDto

    /**
     * 獲取所有流程定義（含分頁）
     */
    public Page<FlowDefinitionListDto> getAllFlowDefinitions(Pageable pageable) {
        // 直接使用 Projection 查詢，單次 SQL 即可完成
        return flowDefinitionRepository.findAllProjection(pageable);
    }

    /**
     * 獲取單個流程定義
     */
    public FlowDefinitionDto getFlowDefinitionById(UUID id) {
        return flowDefinitionRepository.findByIdWithCreatedByAndRoles(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Flow Definition not found!"));
    }

    /**
     * 創建新的流程定義
     */
    @Transactional
    public FlowDefinitionDto createFlowDefinition(CreateFlowDefinitionRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        FlowDefinition flowDef = FlowDefinition.builder()
                .name(request.getName())
                .key(request.getKey())
                .version(request.getVersion())
                .definition(request.getDefinition())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .createdBy(currentUser)
                .createdAt(now)
                .build();

        flowDef = flowDefinitionRepository.save(flowDef);
        return mapToDto(flowDef);
    }

    /**
     * 更新流程定義
     */
    @Transactional
    public FlowDefinitionDto updateFlowDefinition(UUID id, UpdateFlowDefinitionRequest request) {
        FlowDefinition flowDef = flowDefinitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flow Definition not found!"));

        flowDef.setName(request.getName());
        flowDef.setDefinition(request.getDefinition());
        flowDef.setIsPublished(request.getIsPublished());

        // 雖然沒有 UpdatedAt 欄位，但這是業務邏輯的更新
        flowDefinitionRepository.save(flowDef);
        return mapToDto(flowDef);
    }

    /**
     * 刪除流程定義
     */
    public void deleteFlowDefinition(UUID id) {
        if (!flowDefinitionRepository.existsById(id)) {
            throw new RuntimeException("Flow Definition not found!");
        }
        flowDefinitionRepository.deleteById(id);
    }

    /**
     * 實體轉換為 DTO 的輔助方法
     */
    private FlowDefinitionDto mapToDto(FlowDefinition entity) {
        // 由於 FlowDefinition.createdBy 是 Lazy Fetch，必須確保它被載入
        User createdByUser = entity.getCreatedBy();

        return FlowDefinitionDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .key(entity.getKey())
                .version(entity.getVersion())
                .definition(entity.getDefinition())
                .isPublished(entity.getIsPublished())
                .createdAt(entity.getCreatedAt())
                .createdBy(UserMapper.mapToDto(createdByUser))
                .build();
    }
}