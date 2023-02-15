package com.krylov.scrumboard.controller;


import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.helper.FillingSprintDTO;
import com.krylov.scrumboard.helper.MyDateTimeFormatter;
import com.krylov.scrumboard.helper.ProjectOrError;
import com.krylov.scrumboard.request.SprintRequest;
import com.krylov.scrumboard.request.StartProjectRequest;
import com.krylov.scrumboard.service.ProjectService;
import com.krylov.scrumboard.service.SprintService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RestController
@RequestMapping(path = "/api/v1/config")
@AllArgsConstructor
public class ConfigController {

    private final ProjectService projectService;
    private final SprintService sprintService;

    @GetMapping(path = "/project")
    public ResponseEntity<List<Project>> projectConfigurer(ModelAndView modelAndView) {
//        modelAndView.setViewName("project-main");

        return ResponseEntity.ok().body(projectService.getAllProjects());
    }

    @PostMapping(path = "/project")
    public ResponseEntity<Project> createProject(@RequestBody String projectName,
                                                 ModelAndView modelAndView) {
//        modelAndView.setViewName("redirect:/api/v1/config/project");

        Project project = projectService.createProject(projectName);

        return ResponseEntity.ok().body(project);
    }

    @PostMapping(path = "/project/start")
    public ResponseEntity<Project> startProject(@RequestBody StartProjectRequest request,
                                                ModelAndView modelAndView) {
//        modelAndView.setViewName("redirect:/api/v1/project/" + request.getProjectId());

        String start = MyDateTimeFormatter.formatInputDate(request.getSprintStart());

        var sprintRequest = new SprintRequest(start, request.getSprintDuration());
        Project project = projectService.startProjectById(request.getProjectId(), sprintRequest);

        if (project == null)
            return ResponseEntity.status(BAD_REQUEST.value())
                    .header("error", "Project was not found in database with id: " + request.getProjectId())
                    .build();
        return ResponseEntity.ok().body(project);
    }

    @PutMapping(path = "/project/{projectId}")
    public ResponseEntity<Project> stopProject(@PathVariable(name = "projectId") Long id,
                                               ModelAndView modelAndView) {
//        modelAndView.setViewName("redirect:/api/v1/config/project");

        Project project = projectService.stopProject(id);

        if (project == null)
            return ResponseEntity.status(BAD_REQUEST.value())
                    .header("error", "Project was not found in database with id: " + id)
                    .build();
        return ResponseEntity.ok().body(project);
    }

    @DeleteMapping(path = "/project/{projectId}")
    public ResponseEntity<Project> deleteProject(@PathVariable(name = "projectId") Long id,
                                                 ModelAndView modelAndView) {
//        modelAndView.setViewName("redirect:/api/v1/config/project");

        ProjectOrError projectOrError = projectService.deleteProject(id);

        if (projectOrError.getErrorMessage() != null)
            return ResponseEntity.status(BAD_REQUEST.value())
                    .header("error", projectOrError.getErrorMessage())
                    .build();
        return ResponseEntity.ok().body(projectOrError.getProject());
    }


    @PostMapping(path = "/sprint/multiple/{sprintId}")
    public ResponseEntity<Sprint> addMultipleTasksToSprint(@PathVariable(name = "sprintId") Long sprintId,
                                                           @RequestParam(name = "projectId") Long projectId,
                                                           @RequestBody Long[] taskIds,
                                                           ModelAndView modelAndView) {
//        modelAndView.setViewName("redirect:/api/v1/project/" + projectId);

        Sprint sprint = sprintService.addMultipleTasksToSprintById(taskIds, sprintId);

        if (sprint == null)
            return ResponseEntity.status(BAD_REQUEST.value())
                    .header("error", "Could not find sprint by id '" + sprintId + "'")
                    .build();
        return ResponseEntity.ok().body(sprint);
    }

}
