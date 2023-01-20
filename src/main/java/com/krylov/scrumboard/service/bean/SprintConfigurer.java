package com.krylov.scrumboard.service.bean;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.repository.SprintRepository;
import com.krylov.scrumboard.service.helper.Duration;
import com.krylov.scrumboard.service.helper.LocalDateTimeConverter;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SprintConfigurer implements Runnable {

    private LocalDateTimeConverter converter;

    private LocalDateTime startOfSprint;

    private LocalDateTime endOfSprint;

    private Duration sprintDuration;

    private Sprint sprintDraft;

    private List<SprintTask> sprintBacklog;

    private SprintRepository sprintRepository;

    public SprintConfigurer() {
        sprintBacklog = new ArrayList<>();
        sprintDraft = new Sprint();
    }

    private void setSprintProperties() {
        sprintDraft.setStartOfSprint(converter.convertToDatabaseColumn(startOfSprint));
        sprintDraft.setEndOfSprint(converter.convertToDatabaseColumn(endOfSprint));
        sprintDraft.setDuration(sprintDuration);
        sprintDraft.setTaskList(sprintBacklog);

        sprintRepository.save(sprintDraft);
    }

    private void calculateEndOfSprint() {
        endOfSprint = startOfSprint.plusDays(sprintDuration.getDays());
    }

    public void setSprintDuration(Duration sprintDuration) {
        this.sprintDuration = sprintDuration;
        calculateEndOfSprint();
    }

    @SneakyThrows
    public void run() {
        setSprintProperties();

        if (LocalDateTime.now().isAfter(startOfSprint)) {
            sprintDraft = new Sprint();
            Thread.currentThread().interrupt();
            return;
        }
        var tomorrow = LocalDateTime.now().plusDays(1);
        int hrs = tomorrow.getHour();
        int mins = tomorrow.getMinute();
        int secs = tomorrow.getSecond();
        wait(86_400_000 - (secs + mins * 60 + hrs * 3600) * 1000);  // wait until MIDNIGHT of the next day
        run();
    }

    public void addTask(SprintTask task) {
        sprintBacklog.add(task);
    }

    public void addMultipleTasks(List<SprintTask> list) {
        sprintBacklog.addAll(list);
    }

    public void clearBacklog() {
        sprintBacklog = new ArrayList<>();
    }
}
