package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.entity.Task;
import com.krylov.scrumboard.helper.TaskOrError;
import com.krylov.scrumboard.request.UpdateTaskRequest;
import com.krylov.scrumboard.service.TaskService;
import com.krylov.scrumboard.request.TaskRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Service
@RestController
@RequestMapping(path = "/api/v1/task/personal")
@AllArgsConstructor
public class PersonalTaskController {

    private TaskService taskService;

    @GetMapping
    public ResponseEntity<ScrumBoard> showScrumBoard() {
//        modelAndView.setViewName("tasks");

        ScrumBoard board = new ScrumBoard(
                taskService.getAllBacklog(),
                taskService.getAllInProgress(),
                taskService.getAllFinished()
        );

        return ResponseEntity.ok().body(board);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Task> showTaskDetails(@PathVariable(name = "id") Long id) {
//        modelAndView.setViewName("task-details");

        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok().body(task);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest request) {
//        modelAndView.setViewName("redirect:/api/v1/task/personal");
//        modelAndView.addObject("backlogTasks", taskService.retrieveAllBacklog());
//        modelAndView.addObject("inProgressTasks", taskService.retrieveAllInProgress());
//        modelAndView.addObject("finishedTasks", taskService.retrieveAllFinished());


        if (request.getDescription().length() >= 15) {
            return ResponseEntity.ok().body(taskService.save(request));
        }
        else
            return ResponseEntity
                .status(BAD_REQUEST.value())
                .header("error", "Task's description length must be at least 15 characters").build();
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Task> updateTask(@PathVariable(value = "id") Long id,
                                           @RequestBody UpdateTaskRequest request) {
//        modelAndView.setViewName("redirect:/api/v1/task/personal/{id}");
//        modelAndView.addObject("backlogTasks", taskService.retrieveAllBacklog());
//        modelAndView.addObject("inProgressTasks", taskService.retrieveAllInProgress());
//        modelAndView.addObject("finishedTasks", taskService.retrieveAllFinished());
//        return modelAndView;

        TaskOrError taskOrError = taskService.updateTask(id, request);
        if (taskOrError.getErrorMessage() != null)
            return ResponseEntity.status(BAD_REQUEST.value())
                    .header("error", taskOrError.getErrorMessage())
                    .build();
        else
            return ResponseEntity.ok().body(taskOrError.getTask());

    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable(value = "id") Long id) {
//        modelAndView.setViewName("redirect:/api/v1/task/personal");
//        modelAndView.addObject("backlogTasks", taskService.getAllBacklog());
//        modelAndView.addObject("inProgressTasks", taskService.getAllInProgress());
//        modelAndView.addObject("finishedTasks", taskService.getAllFinished());
//        return modelAndView;

        Task task = taskService.deleteTask(id);
        return ResponseEntity.ok().body(task);
    }
}

@Data
@AllArgsConstructor
class ScrumBoard {
    private Collection<Task> backlog;
    private Collection<Task> inProgress;
    private Collection<Task> finished;
}
