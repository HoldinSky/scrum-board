package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.helper.FillingSprintDTO;
import com.krylov.scrumboard.helper.MyDateTimeFormatter;
import com.krylov.scrumboard.helper.Status;
import com.krylov.scrumboard.request.TaskRequest;
import com.krylov.scrumboard.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;


@Service
@RestController
@RequestMapping(path = "/api/v1/project")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping(path = "/{projectId}")
    public ModelAndView showProjectPage(@PathVariable(name = "projectId") Long id,
                                        ModelAndView modelAndView) {
        modelAndView.setViewName("project-details");

        Project project = projectService.retrieveProjectById(id);
        if (project == null) return new ModelAndView("redirect:/api/v1/config/project");

        var backlog = projectService.retrieveBacklog(id);
        modelAndView.addObject("project", project);
        modelAndView.addObject("backlog", backlog);

        if (project.getStatus().equals(Status.PLANNED)) {
            // attributes for starting 'planned' project
            modelAndView.addObject("today", MyDateTimeFormatter.formatToHTMLDate(LocalDate.now()));
            modelAndView.addObject("minDate",
                    MyDateTimeFormatter.formatToHTMLDate(LocalDate.now().minusDays(6)));
            modelAndView.addObject("maxDate",
                    MyDateTimeFormatter.formatToHTMLDate(LocalDate.now().plusDays(30)));

        } else if (project.getStatus().equals(Status.IN_PROGRESS)) {
            // attributes for filling sprints of 'in progress' project
            modelAndView.addObject("currentSprint", projectService.retrieveCurrentSprintById(id));
            modelAndView.addObject("nextSprint", projectService.retrieveNextSprintById(id));

            FillingSprintDTO dto = new FillingSprintDTO(backlog.size());
            modelAndView.addObject("taskIds", dto);

        } else {
            // representation of all passed sprints of 'finished' project
            modelAndView.addObject("allSprints", projectService.retrieveAllSprintsById(id));
        }

        return modelAndView;
    }

    @PostMapping(path = "/{projectId}")
    public ModelAndView createTaskToProjectBacklog(@PathVariable(name = "projectId") Long id,
                                                   @ModelAttribute TaskRequest request,
                                                   ModelAndView modelAndView) {
        if (request.getDescription().length() >= 15)
            projectService.saveTask(request, id);

        modelAndView.setViewName("redirect:/api/v1/project/" + id);
        return modelAndView;
    }

    @GetMapping(path = "/{projectId}/task/{id}")
    public ModelAndView showTaskDetails(@PathVariable(name = "id") Long id,
                                        @PathVariable(name = "projectId") Long projectId,
                                        ModelAndView modelAndView) {

        modelAndView.setViewName("project-task-details");
        modelAndView.addObject("task", projectService.retrieveTaskById(projectId, id));
        return modelAndView;
    }

    @DeleteMapping(path = "/{projectId}/task/{id}")
    public ModelAndView deleteTask(@PathVariable(name = "id") Long id,
                                   @PathVariable(name = "projectId") Long projectId,
                                   ModelAndView modelAndView) {

        projectService.deleteTask(id);

        modelAndView.setViewName("redirect:/api/v1/project/" + projectId);
        return modelAndView;
    }

    @PutMapping(path = "/{projectId}/task/{id}")
    public ModelAndView updateTaskById(@PathVariable(name = "id") Long taskId,
                                       @PathVariable(name = "projectId") Long projectId,
                                       @RequestParam(name = "dif", required = false) Byte difficulty,
                                       @ModelAttribute("request") String request,
                                       ModelAndView modelAndView) {
        projectService.updateTask(taskId, request, difficulty);

        modelAndView.setViewName("redirect:/api/v1/project/" + projectId);
        return modelAndView;
    }


}
