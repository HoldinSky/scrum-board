package com.krylov.scrumboard.service.controller;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.service.logic.ProjectService;
import com.krylov.scrumboard.service.request.SprintTaskRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
@RestController
@RequestMapping(path = "/api/v1/project")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping(path = "/task")
    public List<SprintTask> addTaskToBacklog(@ModelAttribute(name = "task") SprintTaskRequest request) {
        if (request.getDescription().length() > 15)
            projectService.saveTask(request);

        return projectService.retrieveBacklog();
    }

    @PutMapping(path = "task/{id}")
    public List<SprintTask> updateTaskById(@PathVariable(name = "id") Long taskId,
                                       @RequestParam(name = "dif", required = false) Byte difficulty,
                                       @ModelAttribute("request") String request) {
        projectService.updateTask(taskId, request, difficulty);

        return projectService.retrieveBacklog();
    }

    @DeleteMapping(path = "task/{id}")
    public List<SprintTask> deleteTaskById(@PathVariable(name = "id") Long taskId) {
        projectService.deleteTask(taskId);

        return projectService.retrieveBacklog();
    }


}
