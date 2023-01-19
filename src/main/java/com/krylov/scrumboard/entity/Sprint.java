package com.krylov.scrumboard.entity;

import com.krylov.scrumboard.service.helper.Duration;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Sprint")
@Table(name = "Sprint")

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
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

    @Column(name = "time_of_config",
            updatable = false,
            nullable = false)
    private Timestamp canConfigSprint;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH},
            mappedBy = "sprint")
    private List<SprintTask> taskList;

    public Sprint(Timestamp startOfSprint,
                  Timestamp endOfSprint,
                  Duration duration,
                  Timestamp canConfigSprint) {
        this.startOfSprint = startOfSprint;
        this.endOfSprint = endOfSprint;
        this.duration = duration;
        this.canConfigSprint = canConfigSprint;
    }

    public void addSprintTask(SprintTask task) {
        if (taskList == null) taskList = new ArrayList<>();
        taskList.add(task);
    }

}
