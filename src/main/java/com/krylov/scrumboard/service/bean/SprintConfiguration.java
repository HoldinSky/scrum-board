package com.krylov.scrumboard.service.bean;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.Task;
import com.krylov.scrumboard.repository.SprintRepository;
import com.krylov.scrumboard.service.helper.Duration;
import com.krylov.scrumboard.service.helper.LocalDateTimeConverter;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SprintConfiguration implements Runnable {

    private LocalDateTimeConverter converter;

    private LocalDateTime startOfSprint;

    private LocalDateTime endOfSprint;

    private Duration sprintDuration;

    private LocalDateTime configTasksDate;

    private boolean canConfigTasks;

    private Sprint sprintDraft;

    private List<Task> sprintBacklog;

    public SprintConfiguration() {
        canConfigTasks = false;
        sprintBacklog = new ArrayList<>();
    }

    private void calculateEndOfSprint() {
        endOfSprint = startOfSprint.plusDays(sprintDuration.getDays());
        configTasksDate = startOfSprint.minusDays(sprintDuration.getDays() / 3);
    }

    public void setStartOfSprint(LocalDateTime startOfSprint) {
        this.startOfSprint = startOfSprint;
        calculateEndOfSprint();
    }

    public void run() {
        canConfigTasks = LocalDateTime.now().isAfter(configTasksDate) && LocalDateTime.now().isBefore(startOfSprint);
        if (!canConfigTasks) return;

    }

    public void addTask(Task task) {
        sprintBacklog.add(task);
    }

    public void clearBacklog() {
        sprintBacklog = new ArrayList<>();
    }
}
