package com.krylov.scrumboard.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SprintRequest {

    private DateTimeFormatter formatter;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startOfSprint;
    private String sprintDuration;


    public SprintRequest(String start,
                         String duration) {

        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        startOfSprint = LocalDate.parse(start, formatter);
        this.sprintDuration = duration;
    }
}
