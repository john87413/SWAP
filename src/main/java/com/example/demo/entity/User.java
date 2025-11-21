package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "Username", nullable = false, length = 100)
    private String username;

    @Size(max = 200)
    @Nationalized
    @Column(name = "Email", length = 200)
    private String email;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Size(max = 200)
    @Nationalized
    @Column(name = "DisplayName", length = 200)
    private String displayName;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = false;

    @Column(name = "CreatedAt", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(name = "UserRoles",
            joinColumns = @JoinColumn(name = "UserId"),
            inverseJoinColumns = @JoinColumn(name = "RoleId"))
    private Set<Role> roles = new LinkedHashSet<>();

/*
 TODO [Reverse Engineering] create field to map the 'RowVersion' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "RowVersion", columnDefinition = "timestamp not null")
    private Object rowVersion;
*/
}