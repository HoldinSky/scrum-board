package com.krylov.scrumboard.helper;

import com.krylov.scrumboard.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class TaskOrError {
    private Task task;
    private String errorMessage;
}
