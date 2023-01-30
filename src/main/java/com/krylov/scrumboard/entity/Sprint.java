package com.krylov.scrumboard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.krylov.scrumboard.helper.Duration;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode

@JsonIgnoreProperties(value = "taskList")

@Entity(name = "Sprint")
@Table(name = "Sprint")
public class Sprint {

    @Id
    @SequenceGenerator(name = "sprint_sequence",
            sequenceName = "sprint_sequence",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sprint_sequence")
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

    @ManyToOne
    @JoinColumn(name = "project_id",
            foreignKey = @ForeignKey(name = "project_id_fkey"))
    @JsonManagedReference
    private Project project;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH},
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

    public void addSprintTask(SprintTask task) {
        if (taskList == null) taskList = new ArrayList<>();
        taskList.add(task);
    }
}
