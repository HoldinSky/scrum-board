package com.krylov.scrumboard.service.helper;

import com.krylov.scrumboard.entity.Sprint;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SprintServiceProps {

    public Sprint currentSprint;
    public Sprint nextSprint;
    public Duration sprintDuration;
    public SprintProperties properties;
}
