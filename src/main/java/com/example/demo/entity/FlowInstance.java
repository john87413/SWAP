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
@Table(name = "FlowInstances")
public class FlowInstance {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "Id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FlowDefId", nullable = false)
    private FlowDefinition flowDef;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CreatorId", nullable = false)
    private User creator;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @ColumnDefault("'running'")
    @Column(name = "Status", nullable = false, length = 50)
    private String status;

    @Nationalized
    @Lob
    @Column(name = "Data")
    private String data;

    @NotNull
    @ColumnDefault("sysutcdatetime()")
    @Column(name = "StartedAt", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "EndedAt")
    private LocalDateTime endedAt;

/*
 TODO [Reverse Engineering] create field to map the 'RowVersion' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "RowVersion", columnDefinition = "timestamp not null")
    private Object rowVersion;
*/
}