package com.example.demo.repository.user;

import com.example.demo.dto.user.UserDto;
import com.example.demo.dto.user.UserSearchCriteria;
import com.example.demo.entity.QRole;
import com.example.demo.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslUtils;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserDto> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        QUser u = QUser.user;
        QRole r = QRole.role;

        // Base query
        JPQLQuery<UserDto> query = queryFactory
                .select(Projections.constructor(UserDto.class,
                        u.id,
                        u.username,
                        u.email,
                        u.isActive,
                        u.displayName,
                        Expressions.list(r.name).as("roles")
                ))
                .from(u)
                .leftJoin(u.roles, r)
                .where(buildPredicate(criteria));

        // 排序支援
        query = QuerydslUtils.applySorting(query, pageable, u);

        // 分頁查詢
        QueryResults<UserDto> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(u.id)   // 避免 JOIN 為多行
                .fetchResults();

        return new PageImpl<>(
                results.getResults(),
                pageable,
                results.getTotal()
        );
    }

    private BooleanBuilder buildPredicate(UserSearchCriteria c) {
        BooleanBuilder builder = new BooleanBuilder();
        QUser u = QUser.user;
        QRole r = QRole.role;

        if (c.getKeyword() != null && !c.getKeyword().isBlank()) {
            builder.and(
                    u.username.containsIgnoreCase(c.getKeyword())
                            .or(u.email.containsIgnoreCase(c.getKeyword()))
                            .or(u.displayName.containsIgnoreCase(c.getKeyword()))
            );
        }

        if (c.getIsActive() != null) {
            builder.and(u.isActive.eq(c.getIsActive()));
        }

        if (c.getRoleName() != null && !c.getRoleName().isBlank()) {
            builder.and(r.name.eq(c.getRoleName()));
        }

        return builder;
    }
}

