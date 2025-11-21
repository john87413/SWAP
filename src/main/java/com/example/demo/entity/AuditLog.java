package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AuditLogs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FlowInstanceId")
    private FlowInstance flowInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NodeInstanceId")
    private NodeInstance nodeInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private User user;

    @Size(max = 100)
    @Nationalized
    @Column(name = "\"Action\"", length = 100)
    private String action;

    @Nationalized
    @Lob
    @Column(name = "Detail")
    private String detail;

    @NotNull
    @ColumnDefault("sysutcdatetime()")
    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

}