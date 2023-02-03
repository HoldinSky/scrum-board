package com.krylov.scrumboard.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Service
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class MainController {

    @GetMapping
    public ModelAndView showLandingPage(ModelAndView modelAndView) {
        modelAndView.setViewName("main-page");
        return modelAndView;
    }

    @GetMapping("/more")
    public ModelAndView showScrumBoard(ModelAndView modelAndView) {
        modelAndView.setViewName("scrum-board-main");
        return modelAndView;
    }

    @GetMapping("/about")
    public ModelAndView aboutSection(ModelAndView modelAndView) {
        modelAndView.setViewName("about-page");
        return modelAndView;
    }

    @GetMapping("/authenticate")
    public ModelAndView showForm(ModelAndView modelAndView) {
        modelAndView.setViewName("authenticate-form");
        return modelAndView;
    }

    @GetMapping("/invalidSession")
    public ModelAndView invalidSession(ModelAndView modelAndView) {
        modelAndView.setViewName("invalid-session");
        return modelAndView;
    }
}
