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
@Table(name = "Tasks")
public class Task {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "Id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NodeInstanceId", nullable = false)
    private NodeInstance nodeInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignedTo")
    private User assignedTo;

    @Size(max = 100)
    @Nationalized
    @Column(name = "CandidateRole", length = 100)
    private String candidateRole;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @ColumnDefault("'todo'")
    @Column(name = "Status", nullable = false, length = 50)
    private String status;

    @Column(name = "DueAt")
    private LocalDateTime dueAt;

    @Nationalized
    @Lob
    @Column(name = "Payload")
    private String payload;

    @NotNull
    @ColumnDefault("sysutcdatetime()")
    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

}