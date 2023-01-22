package com.krylov.scrumboard.service.bean;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.service.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.service.helper.SprintProperties;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SprintConfigurer {

    private LocalDateTimeConverter converter;

    private SprintProperties properties;

    private Sprint sprintEntity;

    private List<SprintTask> sprintBacklog;

    public SprintConfigurer() {
        sprintBacklog = new ArrayList<>();
        sprintEntity = new Sprint();

        sprintEntity.setTaskList(sprintBacklog);
    }
}
