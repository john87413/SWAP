package com.example.demo.repository.user;

import com.example.demo.dto.user.UserDto;
import com.example.demo.dto.user.UserSearchCriteria;
import com.example.demo.entity.QRole;
import com.example.demo.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 將 Q 實體聲明為成員變數，方便所有方法使用
    private final QUser u = QUser.user;
    private final QRole r = QRole.role;

    // ⭐ 排序屬性映射表：將 DTO/Pageable 屬性名映射到 QueryDSL 的 Q-Entity 屬性
    private static final Map<String, ComparableExpressionBase<?>> SORT_PROPERTIES = Map.of(
            "id", QUser.user.id,
            "username", QUser.user.username,
            "email", QUser.user.email,
            "displayName", QUser.user.displayName,
            "isActive", QUser.user.isActive,
            "createdAt", QUser.user.createdAt,
            "updatedAt", QUser.user.updatedAt
    );

    // 預設排序 (如果 Pageable 中沒有指定)
    private static final Sort.Order DEFAULT_ORDER = Sort.Order.asc("username");


    @Override
    public Page<UserDto> searchUsers(UserSearchCriteria criteria, Pageable pageable) {

        // 判斷是否需要 JOIN roles 表進行過濾
        boolean needsRoleJoin = criteria.getRoleName() != null && !criteria.getRoleName().isBlank();

        // ===== 第一階段：查詢符合條件的 User ID =====
        JPAQuery<UUID> idsQuery = queryFactory
                .select(u.id)
                .from(u);

        // 只有在需要角色過濾時才 JOIN 聯結表
        if (needsRoleJoin) {
            // 注意：這裡只 JOIN 聯結表 r，不是 fetchJoin
            idsQuery = idsQuery.leftJoin(u.roles, r);
        }

        // 套用搜尋條件
        idsQuery = idsQuery.where(buildPredicate(criteria));

        // 如果有 JOIN 角色表進行過濾，必須使用 DISTINCT 去重
        if (needsRoleJoin) {
            idsQuery = idsQuery.distinct();
        }

        // 套用優化後的排序邏輯
        idsQuery = applySorting(idsQuery, pageable);

        // 獲取總數（在分頁前）
        long total = idsQuery.fetchCount();

        // 獲取當前頁的 ID 列表
        List<UUID> userIds = idsQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 如果沒有結果，直接返回空頁
        if (userIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }

        // ===== 第二階段：批次查詢 User 基礎資料 (⭐ DTO 投影) =====
        List<UserDto> userDtos = queryFactory
                .select(
                        // Projections.constructor 實現 DTO 投影，只選取需要的欄位
                        Projections.constructor(UserDto.class,
                                u.id,
                                u.username,
                                u.email,
                                u.displayName,
                                u.isActive
                        )
                )
                .from(u)
                .where(u.id.in(userIds))
                .fetch();

        // 將 List 轉換為 Map，以便按順序組裝
        Map<UUID, UserDto> userMap = userDtos.stream()
                .collect(Collectors.toMap(UserDto::getId, dto -> dto));


        // ===== 第三階段：批次查詢 Roles 資料 (處理多對多關係) =====
        // 專門查詢這些 User ID 擁有的所有角色名稱
        List<Tuple> roleTuples = queryFactory
                .select(u.id, r.name)
                .from(u)
                .leftJoin(u.roles, r) // 執行 JOIN 抓取角色數據
                .where(u.id.in(userIds))
                .orderBy(u.id.asc(), r.name.asc()) // 確保角色名稱有排序
                .fetch();

        // 聚合 Roles 數據到 DTO Map 中
        for (Tuple tuple : roleTuples) {
            UUID userId = tuple.get(u.id);
            String roleName = tuple.get(r.name);

            UserDto dto = userMap.get(userId);
            if (dto != null && roleName != null) {
                // 如果 DTO 的 roles 列表尚未初始化，這裡進行初始化並添加
                if (dto.getRoles() == null) {
                    dto.setRoles(new ArrayList<>());
                }
                dto.getRoles().add(roleName);
            }
        }

        // 根據原始 ID 順序重新排列結果，以保證分頁順序準確
        List<UserDto> results = userIds.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, total);
    }

    /**
     * 構建查詢條件
     */
    private BooleanBuilder buildPredicate(UserSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 關鍵字搜尋
        if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
            String keyword = criteria.getKeyword().trim();
            builder.and(
                    u.username.containsIgnoreCase(keyword)
                            .or(u.email.containsIgnoreCase(keyword))
                            .or(u.displayName.containsIgnoreCase(keyword))
            );
        }

        // 啟用狀態過濾
        if (criteria.getIsActive() != null) {
            builder.and(u.isActive.eq(criteria.getIsActive()));
        }

        // 角色名稱過濾 (在第一階段 JOIN r 時使用)
        if (criteria.getRoleName() != null && !criteria.getRoleName().isBlank()) {
            builder.and(r.name.equalsIgnoreCase(criteria.getRoleName().trim()));
        }

        return builder;
    }

    /**
     * 優化後的套用排序：更通用且簡潔的 QueryDSL 排序
     */
    private <T> JPAQuery<T> applySorting(JPAQuery<T> query, Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().isSorted()
                ? pageable.getSort().toList()
                : List.of(DEFAULT_ORDER);

        // 轉換 Spring Data Sort.Order 到 QueryDSL OrderSpecifier
        List<OrderSpecifier<?>> orderSpecifiers = orders.stream()
                .map(order -> {
                    // 根據屬性名稱查找對應的 QueryDSL 欄位表達式
                    ComparableExpressionBase<?> property = SORT_PROPERTIES.getOrDefault(
                            order.getProperty(),
                            // 如果屬性不存在，使用預設排序屬性
                            SORT_PROPERTIES.get(DEFAULT_ORDER.getProperty())
                    );

                    return order.isAscending()
                            ? property.asc()
                            : property.desc();
                })
                .toList();

        // 套用所有 OrderSpecifiers
        return query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
    }
}