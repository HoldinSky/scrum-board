package com.krylov.scrumboard.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskRequest {

    private String description;
    private Byte difficulty;
}
