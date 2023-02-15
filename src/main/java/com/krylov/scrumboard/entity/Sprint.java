package com.krylov.scrumboard.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.krylov.scrumboard.enums.Duration;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.GenerationType.AUTO;


@Data
@NoArgsConstructor

@JsonIgnoreProperties(value = "taskList")

@Entity(name = "Sprint")
@Table(name = "Sprint")
public class Sprint {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id",
            updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration",
            nullable = false)
    private Duration duration;

    @Column(name = "start",
            updatable = false,
            nullable = false)
    private Timestamp startOfSprint;

    @Column(name = "finish",
            updatable = false,
            nullable = false)
    private Timestamp endOfSprint;

    @ManyToOne(cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinColumn(name = "project_id",
            foreignKey = @ForeignKey(name = "project_id_fkey"))
    @JsonManagedReference
    private Project project;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            mappedBy = "sprint")
    @JsonManagedReference
    private List<SprintTask> taskList;

    public Sprint(Timestamp startOfSprint,
                  Timestamp endOfSprint,
                  Duration duration) {
        this.startOfSprint = startOfSprint;
        this.endOfSprint = endOfSprint;
        this.duration = duration;
    }

}
