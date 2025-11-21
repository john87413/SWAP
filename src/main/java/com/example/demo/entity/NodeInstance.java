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
@Table(name = "NodeInstances")
public class NodeInstance {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "Id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FlowInstanceId", nullable = false)
    private FlowInstance flowInstance;

    @Size(max = 100)
    @Nationalized
    @Column(name = "NodeId", length = 100)
    private String nodeId;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @ColumnDefault("'pending'")
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

}