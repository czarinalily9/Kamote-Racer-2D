package com.kamoteracer.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// record
@Entity
@Table(name = "leaderboard")
public class ScoreEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "initial", nullable = false, length = 25)
    private String initial;

    @Column(nullable = false)
    private Integer score;

    // contructor
    public ScoreEntry() {}

    public ScoreEntry(String initial, Integer score) {
        this.initial = initial;
        this.score = score;
    }

    // getters gunggong
    public Long getUserId() { return userId; }
    public LocalDate getCreatedAt() { return createdAt; }
    public String getInitial() { return initial; }
    public Integer getScore() { return score; }
}