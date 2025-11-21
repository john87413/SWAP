package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class RolePermissionId implements Serializable {
    private static final long serialVersionUID = -3952291847878892611L;
    @NotNull
    @Column(name = "RoleId", nullable = false)
    private Integer roleId;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "PermissionKey", nullable = false, length = 100)
    private String permissionKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RolePermissionId entity = (RolePermissionId) o;
        return Objects.equals(this.roleId, entity.roleId) &&
                Objects.equals(this.permissionKey, entity.permissionKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionKey);
    }

}