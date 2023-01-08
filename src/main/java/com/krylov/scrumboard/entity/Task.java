package com.krylov.scrumboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString

@Entity(name = "Task")
@Table(name = "Task")
public class Task {

    @Id
    @SequenceGenerator(
            name = "task_sequence",
            sequenceName = "task_sequence",
            allocationSize = 1)
    @GeneratedValue(
            generator = "task_sequence",
            strategy = GenerationType.SEQUENCE)
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

    @Column(name = "completed_at")
    private Timestamp completedAt;

    public Task(String description,
                Timestamp createdAt) {
        this.description = description;
        this.createdAt = createdAt;
    }
}
