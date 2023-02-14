package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.request.TaskRequest;
import com.krylov.scrumboard.request.UpdateTaskRequest;
import com.krylov.scrumboard.service.ProjectService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Collection;


@Service
@RestController
@RequestMapping(path = "/api/v1/project")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping(path = "/{projectId}")
    public ResponseEntity<ProjectResponsePage> showProjectPage(@PathVariable(name = "projectId") Long projectId,
                                                               ModelAndView modelAndView) {
//        modelAndView.setViewName("project-details");
//        if (projectService.retrieveProjectById(projectId) == null) return new ModelAndView("redirect:/api/v1/config/project");

        ProjectResponsePage responsePage = new ProjectResponsePage(
                LocalDate.now(),
                projectService.getProjectById(projectId),
                projectService.getBacklog(projectId),
                projectService.getCurrentSprintById(projectId),
                projectService.getNextSprintById(projectId),
                projectService.getAllSprintsById(projectId)
                );

        return ResponseEntity.ok().body(responsePage);
    }

    @PostMapping(path = "/{projectId}")
    public ResponseEntity<Project> createTaskToProjectBacklog(@PathVariable(name = "projectId") Long id,
                                                   @RequestBody TaskRequest request,
                                                   ModelAndView modelAndView) {
//        modelAndView.setViewName("redirect:/api/v1/project/" + id);

        if (request.getDescription().length() >= 15) {
            projectService.saveTask(request, id);
        }

        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok().body(project);
    }

    @DeleteMapping(path = "/{projectId}/task/{id}")
    public ResponseEntity<Project> deleteTask(@PathVariable(name = "id") Long id,
                                   @PathVariable(name = "projectId") Long projectId,
                                   ModelAndView modelAndView) {
//        modelAndView.setViewName("redirect:/api/v1/project/" + projectId);

        projectService.deleteTask(id);

        Project project = projectService.getProjectById(projectId);
        return ResponseEntity.ok().body(project);
    }

    @PutMapping(path = "/{projectId}/task/{id}")
    public ResponseEntity<Project> updateTask(@PathVariable(name = "id") Long taskId,
                                       @PathVariable(name = "projectId") Long projectId,
                                       @RequestBody UpdateTaskRequest request,
                                       ModelAndView modelAndView) {
        modelAndView.setViewName("redirect:/api/v1/project/" + projectId);

        projectService.updateTask(taskId, request);

        return ResponseEntity.ok().body(projectService.getProjectById(projectId));
    }



    @Data @AllArgsConstructor
    private static class ProjectResponsePage {
        private LocalDate today;
        private Project project;
        private Collection<SprintTask> backlog;
        private Sprint current;
        private Sprint next;
        private Collection<Sprint> allSprints;
    }

}
