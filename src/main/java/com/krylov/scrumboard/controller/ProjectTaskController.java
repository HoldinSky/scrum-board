package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.request.SprintTaskRequest;
import com.krylov.scrumboard.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
@Controller
@RequestMapping(path = "/api/v1/task")
@AllArgsConstructor
public class ProjectTaskController {

    private final ProjectService projectService;

    @PostMapping
    public String addTaskToBacklog(@ModelAttribute(name = "task") SprintTaskRequest request) {
        if (request.getDescription().length() > 15)
            projectService.saveTask(request);

        return "redirect:/api/v1/project";
    }

    @PutMapping(path = "/{id}")
    public String updateTaskById(@PathVariable(name = "id") Long taskId,
                                           @RequestParam(name = "dif", required = false) Byte difficulty,
                                           @ModelAttribute("request") String request) {
        projectService.updateTask(taskId, request, difficulty);

        return "redirect:/api/v1/project";
    }

    @DeleteMapping(path = "/{id}")
    public String deleteTaskById(@PathVariable(name = "id") Long taskId) {
        projectService.deleteTask(taskId);

        return "redirect:/api/v1/project";
    }

}
