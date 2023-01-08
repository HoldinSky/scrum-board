package com.krylov.scrumboard.service.controllers;

import com.krylov.scrumboard.service.logic.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@AllArgsConstructor
public class CreateController {

    private TaskService taskService;

    @PostMapping(path = "/addTask")
    public void createTask(@RequestBody String request) {
        taskService.save(request);
    }
}
