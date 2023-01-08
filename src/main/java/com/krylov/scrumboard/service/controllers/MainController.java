package com.krylov.scrumboard.service.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@Controller
public class MainController {

    @GetMapping(path = "/")
    public String showLandingPage(Model model,
                                  @RequestParam(required = false, defaultValue = "World", name = "name") String name) {
        model.addAttribute("name", name);
        return "main-page";
    }
}
