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

@Entity(name = "SprintTask")
@Table(name = "SprintTask")
public class SprintTask {

    @Id
    @SequenceGenerator(
            name = "sprint_task_sequence",
            sequenceName = "sprint_task_sequence",
            allocationSize = 1)
    @GeneratedValue(
            generator = "sprint_task_sequence",
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

    @Column(name = "priority",
            nullable = false)
    private Byte priority;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE},
            mappedBy = "task")
    private List<Worker> workerList;

    public SprintTask(String description) {
        this.description = description;
    }

    public SprintTask(String description,
                      Timestamp createdAt,
                      Byte priority) {
        this.description = description;
        this.createdAt = createdAt;
        this.priority = priority;
    }

    public void addWorker(Worker worker) {
        if (workerList == null) workerList = new ArrayList<>();
        workerList.add(worker);
    }

    public void removeWorker(Worker worker) {
        if (workerList != null) workerList.remove(worker);
    }
}
