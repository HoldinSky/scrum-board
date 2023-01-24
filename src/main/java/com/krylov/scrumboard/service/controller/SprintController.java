package com.krylov.scrumboard.service.controller;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.service.logic.SprintService;
import com.krylov.scrumboard.service.request.SprintRequest;
import com.krylov.scrumboard.service.request.SprintTaskRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Service
@RestController
@RequestMapping(path = "api/v1/sprint")
@AllArgsConstructor
public class SprintController {

    private final SprintService sprintService;

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

        Optional<Sprint> currentOptional = sprintService.getSprint("current");
        Optional<Sprint> nextOptional = sprintService.getSprint("next");

        if (currentOptional.isEmpty() || nextOptional.isEmpty()) {
            modelAndView.addObject("noCurrentSprintMsg", "Sprint was not configured yet");
        } else {
            Sprint current = currentOptional.get();
            Sprint next = nextOptional.get();
            var currentId = current.getId();
            var nextId = next.getId();

            modelAndView.addObject("currentSprint",
                    sprintService.retrieveTaskOfSprint("current"));
            modelAndView.addObject("nextSprint",
                    sprintService.retrieveTaskOfSprint("next"));
        }


//        return modelAndView;
        return List.of(sprintService.getSprint("current").get(), sprintService.getSprint("next").get());
    }

    @PostMapping(path = "/task")
    public List<Sprint> addTaskToBacklog(@ModelAttribute(name = "task") SprintTaskRequest request) {
        if (request.getDescription().length() > 15)
            sprintService.saveTask(request);

        return showCurrentAndNextSprints();
    }

    @PostMapping(path = "/single/{state}")
    public List<Sprint> addOneTaskToSprint(@PathVariable(name = "state") String state,
                                             @RequestParam(name = "task") Long taskId) {
        sprintService.addTaskToSprintById(taskId, state);

        return showCurrentAndNextSprints();
    }


    @PostMapping(path = "/multiple/{state}")
    public List<Sprint> addMultipleTasksToSprint(@PathVariable(name = "state") String state,
                                                   @RequestBody List<Long> taskIds) {

        sprintService.addMultipleTasksToSprintById(taskIds, state);

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
