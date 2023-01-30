package com.krylov.scrumboard.bean;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.helper.SprintProperties;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class SprintConfigurer {

    private LocalDateTimeConverter converter;

    private SprintProperties properties;
    private Sprint sprintEntity;

    private List<SprintTask> sprintBacklog;

    public void setProperties(SprintProperties properties) {
        sprintEntity = new Sprint();
        sprintEntity.setStartOfSprint(converter.convertToDatabaseColumn(properties.getStart().atStartOfDay()));
        sprintEntity.setEndOfSprint(converter.convertToDatabaseColumn(properties.getEnd().atStartOfDay()));
        sprintEntity.setDuration(properties.getDuration());
        sprintEntity.setTaskList(new ArrayList<>());
    }
}
