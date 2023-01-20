package com.krylov.scrumboard.service.controller;

import com.krylov.scrumboard.service.logic.SprintService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Service
@RestController
@RequestMapping(path = "api/v1/sprint")
@AllArgsConstructor
public class SprintController {

    private SprintService sprintService;

    @GetMapping
    public ModelAndView showSprintConfiguratorAndBacklog() {
        var modelAndView = new ModelAndView("sprint-main");

        modelAndView.addObject("backlog", sprintService.retrieveBacklog());

        return modelAndView;
    }



}
