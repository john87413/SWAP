package com.example.demo.repository.user;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {
    @Query(
            value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles",
            countQuery = "SELECT COUNT(DISTINCT u) FROM User u"
    )
    Page<User> findAllWithRoles(Pageable pageable);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}