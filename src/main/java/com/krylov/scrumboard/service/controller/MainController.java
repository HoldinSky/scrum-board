package com.krylov.scrumboard.service.controller;

import com.krylov.scrumboard.service.logic.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Service
@RestController
@RequestMapping(path = "api/v1")
@AllArgsConstructor
public class MainController {

    private TaskService taskService;

    @GetMapping
    public ModelAndView showLandingPage(@RequestParam(required = false, defaultValue = "World", name = "name") String name) {
        ModelAndView modelAndView = new ModelAndView("main-page");
        modelAndView.addObject("name", name);
        return modelAndView;
    }

    @GetMapping(path = "/board")
    public ModelAndView showScrumBoard() {
        ModelAndView modelAndView = new ModelAndView("scrum-board-main");
        modelAndView.addObject("tasks", taskService.retrieveALl());
        return modelAndView;
    }
}
