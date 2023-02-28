package com.krylov.scrumboard.helper;

import com.krylov.scrumboard.entity.Task;

public record TaskOrError(Task task, String errorMessage) {
}
