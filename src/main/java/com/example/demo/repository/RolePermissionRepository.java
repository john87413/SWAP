package com.example.demo.repository;

import com.example.demo.entity.RolePermission;
import com.example.demo.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
}