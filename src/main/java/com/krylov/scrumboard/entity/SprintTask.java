package com.krylov.scrumboard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

@JsonIgnoreProperties(value = {"workerList"})

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
    @JsonManagedReference
    private List<Worker> workerList;

    @ManyToOne
    @JoinColumn(name = "sprint_id",
            foreignKey = @ForeignKey(name = "sprint_id_fkey"))
    @JsonBackReference
    private Sprint sprint;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "project_id",
            foreignKey = @ForeignKey(name = "project_id_fkey"))
    private Project project;

    public SprintTask(String description,
                      Timestamp createdAt,
                      Byte priority,
                      Project project) {
        this.description = description;
        this.createdAt = createdAt;
        this.priority = priority;
        this.project = project;
    }

    public void addWorker(Worker worker) {
        if (workerList == null) workerList = new ArrayList<>();
        workerList.add(worker);
    }

    public void removeWorker(Worker worker) {
        if (workerList != null) workerList.remove(worker);
    }
}
