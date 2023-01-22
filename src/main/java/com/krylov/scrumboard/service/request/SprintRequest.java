package com.krylov.scrumboard.service.request;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SprintRequest {

    private DateTimeFormatter formatter;
    private LocalDate startOfSprint;
    private String sprintDuration;


    public SprintRequest(String start,
                         String duration) {

        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        startOfSprint = LocalDate.parse(start, formatter);
        this.sprintDuration = duration;
    }
}
