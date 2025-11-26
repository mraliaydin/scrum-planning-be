package com.thy.scrum_planning_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "votes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String point; // String to handle non-numeric values such as ?

    @Column(name = "voted_at")
    @Builder.Default
    private LocalDateTime votedAt = LocalDateTime.now();

    @Column(name = "is_revealed")
    @Builder.Default
    private boolean isRevealed = false;
}
