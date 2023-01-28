package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Service
@RestController
@RequestMapping(path = "/api/v1/project")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping(path = "/{projectId}")
    public ModelAndView showProjectPage(@PathVariable(name = "projectId") Long id) {
        var modelAndView = new ModelAndView("project-details");

        modelAndView.addObject("project", projectService.retrieveProjectById(id));
        modelAndView.addObject("backlog", projectService.retrieveBacklog(id));

        return modelAndView;
    }

    @GetMapping(path = "/task/{id}")
    public ModelAndView showTaskDetails(@PathVariable(name = "id") Long id) {
        var modelAndView = new ModelAndView("task-details");

        modelAndView.addObject("task", projectService.retrieveTaskById(id));
        return modelAndView;
    }

}
