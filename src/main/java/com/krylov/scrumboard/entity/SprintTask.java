package com.krylov.scrumboard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.AUTO;
import static jakarta.persistence.CascadeType.*;

@Data
@NoArgsConstructor

@JsonIgnoreProperties(value = {"workerList"})

@Entity(name = "SprintTask")
@Table(name = "sprint_task")
public class SprintTask {

    @Id @GeneratedValue(strategy = AUTO)
    @Column(name = "id",
            updatable = false)
    private Long id;

    @Column(name = "description",
            nullable = false)
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


    @ManyToOne(cascade = {PERSIST, REFRESH, MERGE, DETACH},
            fetch = LAZY)
    @JoinColumn(name = "sprint_id",
            foreignKey = @ForeignKey(name = "sprint_id_fkey"))
    @JsonBackReference
    private Sprint sprint;

    @ManyToOne(fetch = LAZY,
            cascade = {REFRESH, MERGE, PERSIST, DETACH})
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

}
