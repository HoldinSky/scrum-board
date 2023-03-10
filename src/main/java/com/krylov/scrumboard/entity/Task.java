package com.krylov.scrumboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor

@Entity(name = "Task")
@Table(name = "task")
public class Task {

    @Id @GeneratedValue(strategy = AUTO)
    @Column(name = "id",
            updatable = false)
    private Long id;

    @Column(name = "description",
            nullable = false,
            length = 255)
    private String description;

    @Column(name = "created_at",
            nullable = false,
            updatable = false)
    private Timestamp createdAt;

    @Column(name = "started_at")
    private Timestamp startedAt;

    @Column(name = "finished_at")
    private Timestamp finishedAt;

    @Column(name = "difficulty")
    private Byte difficulty;

    @Column(name = "priority",
            nullable = false)
    private Byte priority;

    @ManyToOne
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "user_id_fkey"))
    private AppUser user;

    public Task(String description) {
        this.description = description;
    }

    public Task(String description,
                Timestamp createdAt,
                Byte priority) {
        this.description = description;
        this.createdAt = createdAt;
        this.priority = priority;
    }
}
