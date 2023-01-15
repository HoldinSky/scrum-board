package com.krylov.scrumboard.service.controller;

import com.krylov.scrumboard.entity.Task;
import com.krylov.scrumboard.service.helper.TaskToShow;
import com.krylov.scrumboard.service.logic.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Service
@RestController
@RequestMapping(path = "api/v1/tasks")
@AllArgsConstructor
public class TaskController {

    private TaskService taskService;

    @PostMapping
    public Task createTask(@RequestBody String request) {
        return taskService.save(request);
    }

    @PatchMapping(path = "{id}")
    public Task updateTask(@PathVariable(value = "id") Long id,
                              @RequestParam(name = "dif", required = false, defaultValue = "0") Byte difficulty,
                              @RequestBody String request) {
        return taskService.updateTask(id, difficulty, request);
    }

    @DeleteMapping(path = "{id}")
    public Task deleteTask(@PathVariable(value = "id") Long id) {
        return taskService.deleteTask(id);
    }

    @GetMapping
    public ModelAndView showScrumBoard() {
        ModelAndView modelAndView = new ModelAndView("task-main.html");
        modelAndView.addObject("tasks", taskService.retrieveALl());
        return modelAndView;
    }
}
