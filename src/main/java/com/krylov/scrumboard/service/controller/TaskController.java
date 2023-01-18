package com.krylov.scrumboard.service.controller;

import com.krylov.scrumboard.service.logic.TaskService;
import com.krylov.scrumboard.service.request.TaskRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Service
@RestController
@RequestMapping(path = "api/v1/tasks")
@AllArgsConstructor
public class TaskController {

    private TaskService taskService;

    @GetMapping
    public ModelAndView showScrumBoard() {
        ModelAndView modelAndView = new ModelAndView("task-main");
        modelAndView.addObject("backlogTasks", taskService.retrieveAllBacklog());
        modelAndView.addObject("inProgressTasks", taskService.retrieveAllInProgress());
        modelAndView.addObject("finishedTasks", taskService.retrieveAllFinished());
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createTask(@ModelAttribute TaskRequest request) {
        if (request.getDescription().length() > 15)
            taskService.save(request);

        ModelAndView modelAndView = new ModelAndView("redirect:/api/v1/tasks");
        modelAndView.addObject("backlogTasks", taskService.retrieveAllBacklog());
        modelAndView.addObject("inProgressTasks", taskService.retrieveAllInProgress());
        modelAndView.addObject("finishedTasks", taskService.retrieveAllFinished());
        return modelAndView;
    }

    @PutMapping(path = "{id}")
    public ModelAndView updateTask(@PathVariable(value = "id") Long id,
                           @RequestParam(name = "dif", required = false) Byte difficulty,
                           @ModelAttribute("request") String request) {
        taskService.updateTask(id, difficulty, request);

        ModelAndView modelAndView = new ModelAndView("redirect:/api/v1/tasks");
        modelAndView.addObject("backlogTasks", taskService.retrieveAllBacklog());
        modelAndView.addObject("inProgressTasks", taskService.retrieveAllInProgress());
        modelAndView.addObject("finishedTasks", taskService.retrieveAllFinished());
        return modelAndView;
    }

    @DeleteMapping(path = "{id}")
    public void deleteTask(@PathVariable(value = "id") Long id) {
        taskService.deleteTask(id);
    }

}
