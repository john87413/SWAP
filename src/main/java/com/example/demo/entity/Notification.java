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
@Table(name = "Notifications")
public class Notification {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "Id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @Size(max = 200)
    @Nationalized
    @Column(name = "Title", length = 200)
    private String title;

    @Nationalized
    @Lob
    @Column(name = "Content")
    private String content;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "IsRead", nullable = false)
    private Boolean isRead = false;

    @NotNull
    @ColumnDefault("sysutcdatetime()")
    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

}