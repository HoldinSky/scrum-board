package com.krylov.scrumboard.helper;

import com.krylov.scrumboard.entity.SprintTask;

public record SprintTaskOrError(SprintTask task, String errorMessage) {
}
