package com.krylov.scrumboard.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SprintTaskRequest {

    private String description;
    private Byte priority;
    private Byte difficulty;
}
