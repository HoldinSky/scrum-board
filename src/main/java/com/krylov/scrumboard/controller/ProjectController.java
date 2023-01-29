package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.helper.MyDateTimeFormatter;
import com.krylov.scrumboard.request.TaskRequest;
import com.krylov.scrumboard.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;


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
        modelAndView.addObject("today", MyDateTimeFormatter.formatToHTMLDate(LocalDate.now()));
        modelAndView.addObject("minDate",
                MyDateTimeFormatter.formatToHTMLDate(LocalDate.now().minusDays(7)));
        modelAndView.addObject("maxDate",
                MyDateTimeFormatter.formatToHTMLDate(LocalDate.now().plusDays(30)));

        return modelAndView;
    }

    @PostMapping(path = "/{projectId}")
    public ModelAndView createTaskToProjectBacklog(@PathVariable(name = "projectId") Long id,
                                                   @ModelAttribute TaskRequest request) {
        if (request.getDescription().length() > 15)
            projectService.saveTask(request, id);

        return new ModelAndView("redirect:/api/v1/project/" + id);
    }

    @GetMapping(path = "/task/{projectId}/{id}")
    public ModelAndView showTaskDetails(@PathVariable(name = "id") Long id,
                                        @PathVariable(name = "projectId") Long projectId) {
        var modelAndView = new ModelAndView("project-task-details");

        modelAndView.addObject("task", projectService.retrieveTaskById(projectId, id));
        return modelAndView;
    }

    @DeleteMapping(path = "/task/{projectId}/{id}")
    public ModelAndView deleteTask(@PathVariable(name = "id") Long id,
                                   @PathVariable(name = "projectId") Long projectId) {

        projectService.deleteTask(id);

        return new ModelAndView("redirect:/api/v1/project/" + projectId);
    }

    @PutMapping(path = "/task/{projectId}/{id}")
    public ModelAndView updateTaskById(@PathVariable(name = "id") Long taskId,
                                       @PathVariable(name = "projectId") Long projectId,
                                       @RequestParam(name = "dif", required = false) Byte difficulty,
                                       @ModelAttribute("request") String request) {
        projectService.updateTask(taskId, request, difficulty);

        return new ModelAndView("redirect:/api/v1/project/" + projectId);
    }


}
