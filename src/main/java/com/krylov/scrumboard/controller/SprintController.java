package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.service.SprintService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Service
@RestController
@RequestMapping(path = "api/v1/sprint")
@AllArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @GetMapping(path = "/{id}")
    public ModelAndView showSprintTask(@PathVariable(name = "id") Long id,
                                       ModelAndView modelAndView) {
        modelAndView.setViewName("sprint-task-details");

        modelAndView.addObject("task", sprintService.getSprintTask(id));

        return modelAndView;
    }


    @GetMapping(path = "/upcoming/{projectId}")
    public ModelAndView showProjectSprints(@PathVariable(name = "projectId") Long id,
                                           ModelAndView modelAndView) {
        modelAndView.setViewName("sprint-main");

        modelAndView.addObject("currentSprint",
                sprintService.getSprintOfProject(id, "current"));
        modelAndView.addObject("nextSprint",
                sprintService.getSprintOfProject(id, "next"));

        return modelAndView;
    }

    @PutMapping(path = "/{taskId}")
    public ModelAndView updateTask(@PathVariable(name = "taskId") Long id,
                                   @ModelAttribute(name = "action") String action,
                                   @RequestParam(required = false, defaultValue = "0", name = "dif") Byte difficulty,
                                   ModelAndView modelAndView) {
        modelAndView.setViewName("sprint-main");

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

        modelAndView.addObject("sprint", sprintService.getSprintById(id));

        return modelAndView;
    }

    @DeleteMapping
    public ModelAndView deleteTask(@ModelAttribute(name = "taskId") Long id,
                                   ModelAndView modelAndView) {
        modelAndView.setViewName("sprint-main");

        sprintService.deleteTask(id);

        modelAndView.addObject("sprint", sprintService.getSprintById(id));

        return modelAndView;
    }

}
