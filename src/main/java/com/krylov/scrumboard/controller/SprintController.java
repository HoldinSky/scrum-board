package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.helper.SprintTaskOrError;
import com.krylov.scrumboard.request.UpdateTaskRequest;
import com.krylov.scrumboard.service.ProjectService;
import com.krylov.scrumboard.service.SprintService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Service
@RestController
@RequestMapping(path = "/api/sprint")
@AllArgsConstructor
public class SprintController {

    private final SprintService sprintService;
    private final ProjectService projectService;


    @GetMapping(path = "/{taskId}")
    public ResponseEntity<SprintTaskDetails> showSprintTask(@PathVariable(name = "taskId") Long id) {
//        modelAndView.setViewName("sprint-task-details");

        var task = sprintService.getSprintTask(id);

        if (task == null)
            return ResponseEntity.status(BAD_REQUEST.value())
                    .header("error", "Task is not found in database with id: " + id).build();
        else {
            var sprint = task.getSprint();
            return ResponseEntity.ok(new SprintTaskDetails(task, sprint));
        }
    }


    @GetMapping(path = "/upcoming/{projectId}")
    public ResponseEntity<ProjectPage> showProjectSprints(@PathVariable(name = "projectId") Long id) {
//        modelAndView.setViewName("sprint-main");

        var current = sprintService.getSprintOfProject(id, "current");
        var next = sprintService.getSprintOfProject(id, "next");

        var response = new ProjectPage(
                projectService.getProjectById(id),
                current,
                next,
                sprintService.getBacklogOfSprint(current.getId()),
                sprintService.getInProgressOfSprint(current.getId()),
                sprintService.getFinishedOfSprint(current.getId()),
                sprintService.getTasksOfSprint(next.getId())
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/{taskId}")
    public ResponseEntity<SprintTask> updateTask(@PathVariable(name = "taskId") Long id,
                                                 @RequestBody UpdateTaskRequest request) {
//        Project project = sprintService.getSprintTask(id).getSprint().getProject();
//        modelAndView.setViewName("redirect:/api/v1/sprint/upcoming/" + project.getId());

        SprintTaskOrError taskOrError = sprintService.updateTaskById(id, request);
        if (taskOrError.errorMessage() != null)
            return ResponseEntity.status(BAD_REQUEST.value())
                    .header("error", taskOrError.errorMessage()).build();
        else
            return ResponseEntity.ok(taskOrError.task());
    }

    @DeleteMapping(path = "/{taskId}")
    public ResponseEntity<SprintTask> deleteTask(@PathVariable(name = "taskId") Long id) {
//        modelAndView.setViewName("redirect:/api/v1/sprint/upcoming/" + projectId);

        SprintTask task = sprintService.deleteTask(id);

        return ResponseEntity.ok(task);
    }

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SprintTaskDetails {
    private SprintTask task;
    private Sprint sprint;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ProjectPage {

    private Project project;
    private Sprint currentSprint;
    private Sprint nextSprint;
    private List<SprintTask> currentBacklog;
    private List<SprintTask> currentInProgress;
    private List<SprintTask> currentFinished;
    private List<SprintTask> nextBacklog;
}
