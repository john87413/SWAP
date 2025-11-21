package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "FlowDefinitions")
public class FlowDefinition {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "Id", nullable = false)
    private UUID id;

    @Size(max = 200)
    @NotNull
    @Nationalized
    @Column(name = "Name", nullable = false, length = 200)
    private String name;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "\"Key\"", nullable = false, length = 100)
    private String key;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "Version", nullable = false)
    private Integer version;

    @NotNull
    @Nationalized
    @Lob
    @Column(name = "Definition", nullable = false)
    private String definition;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CreatedBy", nullable = false)
    private User createdBy;

    @NotNull
    @ColumnDefault("sysutcdatetime()")
    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "IsPublished", nullable = false)
    private Boolean isPublished = false;

}