package com.krylov.scrumboard.helper;

import com.krylov.scrumboard.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectOrError {
    private Project project;
    private String errorMessage;
}
