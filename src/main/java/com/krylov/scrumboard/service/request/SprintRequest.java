package com.krylov.scrumboard.service.request;


import com.krylov.scrumboard.service.helper.Duration;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SprintRequest {

    private DateTimeFormatter formatter;

    private LocalDateTime startOfSprint;
    private String sprintDuration;


    public SprintRequest(String dateTime,
                         String duration) {

        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        startOfSprint = LocalDateTime.parse(dateTime, formatter);
        this.sprintDuration = duration;
    }
}
