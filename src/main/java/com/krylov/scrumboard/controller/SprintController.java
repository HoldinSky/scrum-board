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
    public ModelAndView showSprintBacklog(@PathVariable(name = "id") Long id,
                                          ModelAndView modelAndView) {
        modelAndView.setViewName("sprint-details");

        modelAndView.addObject("sprint", sprintService.getSprintById(id));
        modelAndView.addObject("sprint", sprintService.getSprintById(id));

        return modelAndView;
    }

    @GetMapping
    public ModelAndView showProjectSprints(@ModelAttribute(name = "projectName") String name,
                                           ModelAndView modelAndView) {
        modelAndView.setViewName("sprint-main");

        modelAndView.addObject("currentSprint",
                sprintService.retrieveTaskOfSprintOfProject(name, "current"));
        modelAndView.addObject("nextSprint",
                sprintService.retrieveTaskOfSprintOfProject(name, "next"));

        return modelAndView;
    }

    @PostMapping
    public ModelAndView startTask(@ModelAttribute(name = "taskId") Long id,
                                  ModelAndView modelAndView) {
        modelAndView.setViewName("sprint-main");

        sprintService.startTaskById(id);

        return modelAndView;
    }

}
