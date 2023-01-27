package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.service.TaskService;
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
        var modelAndView = new ModelAndView("main-page");
        modelAndView.addObject("name", name);
        return modelAndView;
    }

    @GetMapping(path = "/main")
    public ModelAndView showScrumBoard() {
        return new ModelAndView("scrum-board-main");
    }
}
