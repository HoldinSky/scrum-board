package com.krylov.scrumboard.helper;

import com.krylov.scrumboard.entity.SprintTask;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FillingSprintDTO {

    private final List<SprintTask> taskList;

    public FillingSprintDTO() {
        taskList = new ArrayList<>();
    }

    public FillingSprintDTO(int size) {
        this.taskList = new ArrayList<>(size);
    }

    public void addId(SprintTask task) {
        this.taskList.add(task);
    }
}
