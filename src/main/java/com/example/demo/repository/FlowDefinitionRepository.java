package com.example.demo.repository;

import com.example.demo.dto.flow.FlowDefinitionListDto;
import com.example.demo.entity.FlowDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 新增
import org.springframework.data.repository.query.Param; // 新增

import java.util.Optional;
import java.util.UUID;

public interface FlowDefinitionRepository extends JpaRepository<FlowDefinition, UUID> {

    // 【優化後】使用兩個 JOIN FETCH 語句，一次性載入 createdBy 和 createdBy 的 roles
    // 必須使用 DISTINCT 來確保 JPA 返回單一的 FlowDefinition 物件
    @Query("SELECT DISTINCT fd FROM FlowDefinition fd " +
            "LEFT JOIN FETCH fd.createdBy u " +
            "LEFT JOIN FETCH u.roles " +
            "WHERE fd.id = :id")
    Optional<FlowDefinition> findByIdWithCreatedByAndRoles(@Param("id") UUID id);

    /**
     * 【列表查詢優化】使用 DTO Projection + JOIN
     * 避免載入 LOB 欄位 (Definition)，並消除 CreatedBy 的 N+1 查詢。
     * @param pageable 分頁資訊
     * @return 流程定義列表的 Page<FlowDefinitionListDto>
     */
    @Query("SELECT new com.example.demo.dto.flow.FlowDefinitionListDto(" +
            "  fd.id, fd.name, fd.key, fd.version, fd.isPublished, fd.createdAt, " +
            "  u.username, u.displayName" +
            ") FROM FlowDefinition fd JOIN fd.createdBy u")
    Page<FlowDefinitionListDto> findAllProjection(Pageable pageable);
}