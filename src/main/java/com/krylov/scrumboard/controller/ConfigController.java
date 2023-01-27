package com.krylov.scrumboard.controller;


import com.krylov.scrumboard.request.SprintRequest;
import com.krylov.scrumboard.request.StartProjectRequest;
import com.krylov.scrumboard.service.ProjectService;
import com.krylov.scrumboard.service.SprintService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Service
@RestController
@RequestMapping(path = "/api/v1/config")
@AllArgsConstructor
public class ConfigController {

    private final ProjectService projectService;

    private final SprintService sprintService;

    @GetMapping(path = "/project")
    public ModelAndView projectConfigurer() {
        var modelAndView = new ModelAndView("project-main");

        modelAndView.addObject("projects", projectService.retrieveAllProjects());

        return modelAndView;
    }

    @PostMapping(path = "/project")
    public ModelAndView createProject(@ModelAttribute(name = "projectName") String name) {

        projectService.createProject(name);

        return new ModelAndView("redirect:/api/v1/config/project");
    }

    @PostMapping(path = "/project/start")
    public ModelAndView startProject(@ModelAttribute(name = "startProjectRequest") StartProjectRequest request) {

        var sprintRequest = new SprintRequest(request.getSprintStart(), request.getSprintDuration());

        projectService.startProjectByName(request.getProjectName(), sprintRequest);

        return new ModelAndView("redirect:/api/v1/config/project");
    }

    @PutMapping(path = "/project/{projectName}")
    public ModelAndView stopProject(@PathVariable(name = "projectName") String name) {

        projectService.stopProject(name);

        return new ModelAndView("redirect:/api/v1/config/project");
    }

    @DeleteMapping(path = "/project/{projectName}")
    public ModelAndView deleteProject(@PathVariable(name = "projectName") String name) {

        projectService.deleteProject(name);

        return new ModelAndView("redirect:/api/v1/config/project");
    }


    @GetMapping(path = "/sprint")
    public ModelAndView showSprintConfigurer(@ModelAttribute(name = "projectName") String name) {
        var modelAndView = new ModelAndView("project-details");

        modelAndView.addObject("backlog", projectService.retrieveBacklog(name));

        return modelAndView;
    }


    @PostMapping(path = "/sprint/single/{sprintId}")
    public ModelAndView addOneTaskToSprint(@PathVariable(name = "sprintId") Long sprintId,
                                           @RequestParam(name = "task") Long taskId) {
        sprintService.addTaskToSprintById(taskId, sprintId);

        return new ModelAndView("redirect:/api/v1/config/sprint");
    }


    @PostMapping(path = "/sprint/multiple/{sprintId}")
    public ModelAndView addMultipleTasksToSprint(@PathVariable(name = "sprintId") Long sprintId,
                                                 @RequestBody List<Long> taskIds) {

        sprintService.addMultipleTasksToSprintById(taskIds, sprintId);

        return new ModelAndView("redirect:/api/v1/config/sprint");
    }

}
