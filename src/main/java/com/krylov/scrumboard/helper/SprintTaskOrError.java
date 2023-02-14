package com.krylov.scrumboard.helper;

import com.krylov.scrumboard.entity.SprintTask;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SprintTaskOrError {
    private SprintTask task;
    private String errorMessage;
}
