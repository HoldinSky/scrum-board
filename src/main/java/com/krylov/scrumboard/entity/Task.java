package com.krylov.scrumboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "started_at")
    private Timestamp startedAt;

    @Column(name = "finished_at")
    private Timestamp finishedAt;

    @Column(name = "difficulty")
    private Byte difficulty;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            mappedBy = "task")
    private List<Worker> workerList;

    public Task(String description) {
        this.description = description;
    }

    public Task(String description,
                Timestamp createdAt) {
        this.description = description;
        this.createdAt = createdAt;
    }

    public void addWorker(Worker worker) {
        if (workerList == null) workerList = new ArrayList<>();

        workerList.add(worker);
    }
}
