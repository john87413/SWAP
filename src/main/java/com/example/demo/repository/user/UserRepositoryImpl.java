package com.example.demo.repository.user;

import com.example.demo.dto.user.UserDto;
import com.example.demo.dto.user.UserSearchCriteria;
import com.example.demo.entity.QRole;
import com.example.demo.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 將 Q 實體聲明為靜態，提高可讀性
    private static final QUser u = QUser.user;
    private static final QRole r = QRole.role;

    /** 排序屬性映射 */
    private static final Map<String, ComparableExpressionBase<?>> SORT_PROPERTIES = Map.of(
            "id", u.id,
            "username", u.username,
            "email", u.email,
            "displayName", u.displayName,
            "isActive", u.isActive,
            "createdAt", u.createdAt,
            "updatedAt", u.updatedAt
    );

    /** 預設排序 */
    private static final Sort.Order DEFAULT_ORDER = Sort.Order.asc("username");


    @Override
    public Page<UserDto> searchUsers(UserSearchCriteria criteria, Pageable pageable) {

        boolean filterRoles = hasText(criteria.getRoleName());
        BooleanBuilder userPredicate = buildUserPredicate(criteria);

        // ----------------------------------------------------------------------
        //             1. Count Query（獲取總數）
        // ----------------------------------------------------------------------
        JPAQuery<Long> countQuery = queryFactory
                .select(filterRoles ? u.id.countDistinct() : u.count())
                .from(u)
                .where(userPredicate);

        if (filterRoles) {
            countQuery.leftJoin(u.roles, r)
                    .where(roleEquals(criteria.getRoleName()));
        }

        long total = Optional.ofNullable(countQuery.fetchOne()).orElse(0L);
        if (total == 0) {
            return Page.empty(pageable);
        }

        // ----------------------------------------------------------------------
        //             2. 查詢分頁後的 User ID
        // ----------------------------------------------------------------------

        // A. 構建 SELECT 欄位列表：必須包含 u.id 和所有排序欄位
        List<Expression<?>> selectColumns = new ArrayList<>();
        selectColumns.add(u.id);

        List<Sort.Order> orders = pageable.getSort().isSorted()
                ? pageable.getSort().toList()
                : List.of(DEFAULT_ORDER);

        // 將所有排序欄位加入 SELECT 列表
        for (Sort.Order order : orders) {
            ComparableExpressionBase<?> expr = SORT_PROPERTIES.get(order.getProperty());
            // 避免重複加入
            if (expr != null && !selectColumns.contains(expr)) {
                selectColumns.add(expr);
            }
        }

        // 確保預設排序欄位也被選取
        ComparableExpressionBase<?> defaultExpr = SORT_PROPERTIES.get(DEFAULT_ORDER.getProperty());
        if (defaultExpr != null && !selectColumns.contains(defaultExpr)) {
            selectColumns.add(defaultExpr);
        }

        // B. 構建 ID 查詢 (使用 Tuple 來選取多個欄位)
        JPAQuery<Tuple> idsQuery = queryFactory
                .select(selectColumns.toArray(new Expression[0]))
                .from(u)
                .where(userPredicate);

        // C. 套用 JOIN、過濾和 DISTINCT
        if (filterRoles) {
            idsQuery.leftJoin(u.roles, r)
                    .where(roleEquals(criteria.getRoleName()))
                    .distinct(); // 啟用 DISTINCT
        }

        // D. 套用排序
        applySorting(idsQuery, pageable);

        // E. 執行查詢並從 Tuple 中提取 ID
        List<Tuple> idTuples = idsQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<UUID> userIds = idTuples.stream()
                .map(tuple -> tuple.get(u.id)) // 從 Tuple 中提取 u.id
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // ----------------------------------------------------------------------
        //             3. 批次查詢 User 基本資料（DTO）
        // ----------------------------------------------------------------------
        List<UserDto> userDtos = queryFactory
                .select(Projections.constructor(UserDto.class,
                        u.id,
                        u.username,
                        u.email,
                        u.displayName,
                        u.isActive
                ))
                .from(u)
                .where(u.id.in(userIds))
                .fetch();

        // 預先初始化 roles 列表
        Map<UUID, UserDto> userMap = userDtos.stream()
                .peek(dto -> dto.setRoles(new ArrayList<>()))
                .collect(Collectors.toMap(UserDto::getId, dto -> dto));

        // ----------------------------------------------------------------------
        //             4. 批次查詢所有角色並聚合進 DTO
        // ----------------------------------------------------------------------
        List<Tuple> roleTuples = queryFactory
                .select(u.id, r.name)
                .from(u)
                .leftJoin(u.roles, r)
                .where(u.id.in(userIds))
                .orderBy(u.id.asc(), r.name.asc())
                .fetch();

        roleTuples.forEach(t -> {
            UUID userId = t.get(u.id);
            String roleName = t.get(r.name);
            if (roleName != null) {
                userMap.get(userId).getRoles().add(roleName);
            }
        });

        // 確保依照分頁的 ID 順序回傳
        List<UserDto> results = userIds.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, total);
    }


    // ---------------------------------------------------------
    //                       Predicate 區塊
    // ---------------------------------------------------------

    /**
     * 構建 User 實體自身的查詢條件 (不包含關聯表格條件)
     */
    private BooleanBuilder buildUserPredicate(UserSearchCriteria c) {
        BooleanBuilder builder = new BooleanBuilder();

        if (hasText(c.getKeyword())) {
            String k = c.getKeyword().trim();
            builder.and(
                    u.username.containsIgnoreCase(k)
                            .or(u.email.containsIgnoreCase(k))
                            .or(u.displayName.containsIgnoreCase(k))
            );
        }

        if (c.getIsActive() != null) {
            builder.and(u.isActive.eq(c.getIsActive()));
        }

        return builder;
    }

    /**
     * 構建 Role 實體自身的查詢條件
     */
    private BooleanExpression roleEquals(String roleName) {
        return hasText(roleName) ? r.name.equalsIgnoreCase(roleName.trim()) : null;
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }


    // ---------------------------------------------------------
    //                       排序邏輯
    // ---------------------------------------------------------

    /**
     * 套用排序邏輯，適用於 JPAQuery<T> 或 JPAQuery<Tuple>
     */
    private <T> void applySorting(JPAQuery<T> query, Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().isSorted()
                ? pageable.getSort().toList()
                : List.of(DEFAULT_ORDER);

        orders.stream()
                .map(this::convertToOrderSpecifier)
                .forEach(query::orderBy);
    }

    /**
     * 將 Spring Data Sort.Order 轉換為 QueryDSL OrderSpecifier
     */
    private OrderSpecifier<?> convertToOrderSpecifier(Sort.Order order) {
        ComparableExpressionBase<?> property =
                SORT_PROPERTIES.getOrDefault(order.getProperty(),
                        SORT_PROPERTIES.get(DEFAULT_ORDER.getProperty()));

        return order.isAscending() ? property.asc() : property.desc();
    }
}