package com.krylov.scrumboard.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskRequest {

    private String description;
    private Byte difficulty;
    private Byte priority;

}
