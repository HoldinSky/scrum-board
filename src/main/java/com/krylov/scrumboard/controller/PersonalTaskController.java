package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.service.TaskService;
import com.krylov.scrumboard.request.TaskRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Service
@RestController
@RequestMapping(path = "/api/v1/task/personal")
@AllArgsConstructor
public class PersonalTaskController {

    private TaskService taskService;

    @GetMapping
    public ModelAndView showScrumBoard(ModelAndView modelAndView) {
        modelAndView.setViewName("tasks");
        modelAndView.addObject("backlogTasks", taskService.retrieveAllBacklog());
        modelAndView.addObject("inProgressTasks", taskService.retrieveAllInProgress());
        modelAndView.addObject("finishedTasks", taskService.retrieveAllFinished());
        return modelAndView;
    }

    @GetMapping(path = "{id}")
    public ModelAndView showTaskDetails(@PathVariable(name = "id") Long id,
                                        ModelAndView modelAndView) {
        modelAndView.setViewName("task-details");
        modelAndView.addObject("task", taskService.retrieveById(id));
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createTask(@ModelAttribute TaskRequest request,
                                   ModelAndView modelAndView) {
        if (request.getDescription().length() >= 15)
            taskService.save(request);

        modelAndView.setViewName("redirect:/api/v1/task/personal");
        modelAndView.addObject("backlogTasks", taskService.retrieveAllBacklog());
        modelAndView.addObject("inProgressTasks", taskService.retrieveAllInProgress());
        modelAndView.addObject("finishedTasks", taskService.retrieveAllFinished());
        return modelAndView;
    }

    @PutMapping(path = "{id}")
    public ModelAndView updateTask(@PathVariable(value = "id") Long id,
                                   @RequestParam(name = "dif", required = false) Byte difficulty,
                                   @ModelAttribute("request") String request,
                                   ModelAndView modelAndView) {
        String message = taskService.updateTask(id, difficulty, request);

        modelAndView.setViewName("redirect:/api/v1/task/personal/{id}");
        modelAndView.addObject("backlogTasks", taskService.retrieveAllBacklog());
        modelAndView.addObject("inProgressTasks", taskService.retrieveAllInProgress());
        modelAndView.addObject("finishedTasks", taskService.retrieveAllFinished());
        return modelAndView;
    }

    @DeleteMapping(path = "{id}")
    public ModelAndView deleteTask(@PathVariable(value = "id") Long id,
                                   ModelAndView modelAndView) {
        taskService.deleteTask(id);

        modelAndView.setViewName("redirect:/api/v1/task/personal");
        modelAndView.addObject("backlogTasks", taskService.retrieveAllBacklog());
        modelAndView.addObject("inProgressTasks", taskService.retrieveAllInProgress());
        modelAndView.addObject("finishedTasks", taskService.retrieveAllFinished());
        return modelAndView;
    }

}
