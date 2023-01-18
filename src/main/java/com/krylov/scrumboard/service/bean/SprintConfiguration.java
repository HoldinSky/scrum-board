package com.krylov.scrumboard.service.bean;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.repository.SprintRepository;
import com.krylov.scrumboard.service.helper.Duration;
import com.krylov.scrumboard.service.helper.LocalDateTimeConverter;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SprintConfiguration {

    private SprintRepository sprintRepository;

    private LocalDateTimeConverter converter;

    private LocalDateTime startOfSprint;

    private LocalDateTime endOfSprint;

    private Duration sprintDuration;

    private LocalDateTime configTasksDate;

    public SprintConfiguration() {

    }

    private void calculateEndOfSprint() {
        endOfSprint = startOfSprint.plusDays(sprintDuration.getDays());
        configTasksDate = startOfSprint.plusDays(sprintDuration.getDays() - sprintDuration.getDays() / 3);
    }

    public void createSprintRecord() {
        Timestamp start = converter.convertToDatabaseColumn(startOfSprint);
        Timestamp end = converter.convertToDatabaseColumn(endOfSprint);
        Timestamp canConfig = converter.convertToDatabaseColumn(configTasksDate);

        Sprint sprint = new Sprint(
                start,
                end,
                sprintDuration,
                canConfig);

        sprintRepository.save(sprint);
    }

    synchronized public boolean canConfigNextSprint() {
        return LocalDateTime.now().isAfter(configTasksDate);
    }

    public void setStartOfSprint(LocalDateTime startOfSprint) {
        this.startOfSprint = startOfSprint;
        calculateEndOfSprint();
    }
}
