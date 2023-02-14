package com.krylov.scrumboard.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTaskRequest {
    private String action;
    private Byte difficulty;
}
