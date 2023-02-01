package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.service.SprintService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;


@Service
@RestController
@RequestMapping(path = "api/v1/sprint")
@AllArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    private final LocalDateTimeConverter converter;

    @GetMapping(path = "/{taskId}")
    public ModelAndView showSprintTask(@PathVariable(name = "taskId") Long id,
                                       ModelAndView modelAndView) {
        modelAndView.setViewName("sprint-task-details");

        var task = sprintService.getSprintTask(id);
        var sprint = task.getSprint();
        var startOfSprint = converter.convertToEntityAttribute(sprint.getStartOfSprint());
        var canStart = startOfSprint.isBefore(LocalDateTime.now());

        modelAndView.addObject("task", sprintService.getSprintTask(id));
        modelAndView.addObject("canInteract", canStart);

        return modelAndView;
    }


    @GetMapping(path = "/upcoming/{projectId}")
    public ModelAndView showProjectSprints(@PathVariable(name = "projectId") Long id,
                                           ModelAndView modelAndView) {
        modelAndView.setViewName("sprint-main");

        var current = sprintService.getSprintToShowOfProject(id, "current");
        var next = sprintService.getSprintToShowOfProject(id, "next");

        modelAndView.addObject("project", current.getProject());
        modelAndView.addObject("currentSprint", current);
        modelAndView.addObject("nextSprint", next);
        modelAndView.addObject("currentTasks", sprintService.getTasksOfSprint(current.getId()));
        modelAndView.addObject("nextTasks", sprintService.getTasksOfSprint(next.getId()));

        return modelAndView;
    }

    @PutMapping(path = "/{taskId}")
    public ModelAndView updateTask(@PathVariable(name = "taskId") Long id,
                                   @ModelAttribute(name = "action") String action,
                                   @RequestParam(required = false, defaultValue = "0", name = "dif") Byte difficulty,
                                   ModelAndView modelAndView) {

        switch (action) {
            case "start" -> sprintService.startTaskById(id);
            case "finish" -> sprintService.finishTask(id);
            case "setDifficulty" -> {
                sprintService.setDifficultyToTask(id, difficulty);
                modelAndView.setViewName("sprint-task-details");
                modelAndView.addObject("task", sprintService.getSprintTask(id));

                return modelAndView;
            }
            default -> System.out.println("DEBUG: Could not recognize command '" + action + "'");
        }

        Project project = sprintService.getSprintTask(id).getSprint().getProject();
        modelAndView.setViewName("redirect:/api/v1/sprint/upcoming/" + project.getId());
        return modelAndView;
    }

    @DeleteMapping(path = "/{taskId}")
    public ModelAndView deleteTask(@PathVariable(name = "taskId") Long id,
                                   @ModelAttribute(name = "projectId") Long projectId,
                                   ModelAndView modelAndView) {

        sprintService.deleteTask(id);

        modelAndView.setViewName("redirect:/api/v1/sprint/upcoming/" + projectId);
        return modelAndView;
    }

}
