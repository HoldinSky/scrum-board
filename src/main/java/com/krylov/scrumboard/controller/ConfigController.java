package com.krylov.scrumboard.controller;


import com.krylov.scrumboard.helper.FillingSprintDTO;
import com.krylov.scrumboard.helper.MyDateTimeFormatter;
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
    public ModelAndView projectConfigurer(ModelAndView modelAndView) {

        modelAndView.setViewName("project-main");
        modelAndView.addObject("projects", projectService.retrieveAllProjects());

        return modelAndView;
    }

    @PostMapping(path = "/project")
    public ModelAndView createProject(@ModelAttribute(name = "projectName") String name,
                                      ModelAndView modelAndView) {

        projectService.createProject(name);
        modelAndView.setViewName("redirect:/api/v1/config/project");

        return modelAndView;
    }

    @PostMapping(path = "/project/start")
    public ModelAndView startProject(@ModelAttribute(name = "startProjectRequest") StartProjectRequest request,
                                     ModelAndView modelAndView) {
        String start = MyDateTimeFormatter.formatInputDate(request.getSprintStart());

        var sprintRequest = new SprintRequest(start, request.getSprintDuration());
        projectService.startProjectById(request.getProjectId(), sprintRequest);

        modelAndView.setViewName("redirect:/api/v1/project/" + request.getProjectId());

        return modelAndView;
    }

    @PutMapping(path = "/project/{projectId}")
    public ModelAndView stopProject(@PathVariable(name = "projectId") Long id,
                                    ModelAndView modelAndView) {

        projectService.stopProject(id);

        modelAndView.setViewName("redirect:/api/v1/config/project");
        return modelAndView;
    }

    @DeleteMapping(path = "/project/{projectId}")
    public ModelAndView deleteProject(@PathVariable(name = "projectId") Long id,
                                      ModelAndView modelAndView) {

        projectService.deleteProject(id);

        modelAndView.setViewName("redirect:/api/v1/config/project");
        return modelAndView;
    }


    @PostMapping(path = "/sprint/multiple/{sprintId}")
    public ModelAndView addMultipleTasksToSprint(@PathVariable(name = "sprintId") Long sprintId,
                                                 @ModelAttribute(name = "list") FillingSprintDTO dto,
                                                 @ModelAttribute(name = "projectId") Long projectId,
                                                 ModelAndView modelAndView) {
        List<Long> taskIds = dto.getTaskList();

        sprintService.addMultipleTasksToSprintById(taskIds, sprintId);

        modelAndView.setViewName("redirect:/api/v1/project/" + projectId);
        return modelAndView;
    }

}
