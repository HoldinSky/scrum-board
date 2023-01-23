package com.krylov.scrumboard.service.controller;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.repository.SprintRepository;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.service.logic.SprintService;
import com.krylov.scrumboard.service.request.SprintRequest;
import com.krylov.scrumboard.service.request.SprintTaskRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Service
@RestController
@RequestMapping(path = "api/v1/sprint")
@AllArgsConstructor
public class SprintController {

    private SprintService sprintService;
    private SprintTaskRepository sprintTaskRepository;

    private SprintRepository sprintRepository;

    @GetMapping(path = "/config")
    public List<SprintTask> showSprintConfiguratorAndBacklog() {
        var modelAndView = new ModelAndView("sprint-config");

        modelAndView.addObject("backlog", sprintService.retrieveBacklog());

//        return modelAndView;
        return sprintService.retrieveBacklog();
    }

    @PostMapping(path = "/config")
    public List<Sprint> configureSprint(@ModelAttribute(name = "sprint") SprintRequest request) {
        return sprintService.configureSprint(request);

//        return new ModelAndView("redirect:/api/v1/sprint");

    }

    @GetMapping
    public List<Sprint> showCurrentAndNextSprints() {
        var modelAndView = new ModelAndView("sprint-main");

        // TODO: manage retrieving sprint ids by default
        var currentId = sprintService.getSprint(4L).getId();
        var nextId = sprintService.getSprint(5L).getId();

        modelAndView.addObject("currentSprint",
                sprintTaskRepository.retrieveTasksOfSprintById(currentId));
        modelAndView.addObject("nextSprint",
                sprintTaskRepository.retrieveTasksOfSprintById(nextId));

//        return modelAndView;
        return List.of(sprintService.getSprint(currentId), sprintService.getSprint(nextId));
    }

    @PostMapping(path = "/task")
    public List<Sprint> addTaskToBacklog(@ModelAttribute(name = "task") SprintTaskRequest request) {
        if (request.getDescription().length() > 15)
            sprintService.saveTask(request);

        return showCurrentAndNextSprints();
    }

    @PostMapping(path = "/single/{sprintId}")
    public List<Sprint> addOneTaskToSprint(@PathVariable(name = "sprintId") Long sprintId,
                                             @RequestParam(name = "task") Long taskId) {
        sprintService.addTaskToSprintById(taskId, sprintId);

        return showCurrentAndNextSprints();
    }


    @PostMapping(path = "/multiple/{sprintId}")
    public List<Sprint> addMultipleTasksToSprint(@PathVariable(name = "sprintId") Long sprintId,
                                                   @RequestBody List<Long> taskIds) {

        sprintService.addMultipleTasksToSprintById(taskIds, sprintId);

        return showCurrentAndNextSprints();
    }

    @PutMapping(path = "task/{id}")
    public List<Sprint> updateTaskById(@PathVariable(name = "id") Long taskId,
                                       @RequestParam(name = "dif", required = false) Byte difficulty,
                                       @ModelAttribute("request") String request) {
        sprintService.updateTask(taskId, request, difficulty);

        return showCurrentAndNextSprints();
    }

    @DeleteMapping(path = "task/{id}")
    public List<Sprint> deleteTaskById(@PathVariable(name = "id") Long taskId) {
        sprintService.deleteTask(taskId);

        return showCurrentAndNextSprints();
    }

}
