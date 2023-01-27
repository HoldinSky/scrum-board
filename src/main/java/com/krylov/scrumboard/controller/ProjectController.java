package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Service
@RestController
@RequestMapping(path = "/api/v1/project")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping(path = "/{projectName}")
    public ModelAndView showProjectPage(@PathVariable(name = "projectName") String name) {
        var modelAndView = new ModelAndView("project-details");

        modelAndView.addObject("project", projectService.retrieveProjectByName(name));

        return modelAndView;
    }


}
