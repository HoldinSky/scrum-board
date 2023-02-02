package com.krylov.scrumboard.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Service
@RestController
@RequestMapping(path = "/api/v1/landing")
@AllArgsConstructor
public class MainController {

    @GetMapping
    public ModelAndView showLandingPage(ModelAndView modelAndView) {
        modelAndView.setViewName("main-page");
        return modelAndView;
    }

    @GetMapping(path = "/main")
    public ModelAndView showScrumBoard(ModelAndView modelAndView) {
        modelAndView.setViewName("scrum-board-main");
        return modelAndView;
    }
}
