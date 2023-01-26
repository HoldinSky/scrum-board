package com.krylov.scrumboard.service.controller;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.service.logic.ProjectService;
import com.krylov.scrumboard.service.logic.SprintService;
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

    private final ProjectService projectService;

        private List<Sprint> sprintList;        // FOR TESTING PURPOSES ONLY

    @GetMapping(path = "/config")
    public List<SprintTask> showSprintConfiguratorAndBacklog() {
        var modelAndView = new ModelAndView("sprint-config");

        modelAndView.addObject("backlog", projectService.retrieveBacklog());

//        return modelAndView;
        return projectService.retrieveBacklog();
    }

    @GetMapping
    public List<Sprint> showCurrentAndNextSprints(@ModelAttribute(name = "projectName") String name) {
        var modelAndView = new ModelAndView("sprint-upcoming");

        Project project = projectService.retrieveProjectByName(name);
        if (project.getSprintList() == null) {
//            return modelAndView;
        }

        Optional<Sprint> currentOptional = sprintService.getSprintOfProject(project, "current");
        Optional<Sprint> nextOptional = sprintService.getSprintOfProject(project, "next");

        if (currentOptional.isEmpty() || nextOptional.isEmpty()) {
            modelAndView.addObject("noCurrentSprintMsg", "Sprint was not configured yet");
        } else {
            Sprint current = currentOptional.get();
            Sprint next = nextOptional.get();
            var currentId = current.getId();
            var nextId = next.getId();

            modelAndView.addObject("currentSprint",
                    sprintService.retrieveTaskOfSprintOfProject(project, "current"));
            modelAndView.addObject("nextSprint",
                    sprintService.retrieveTaskOfSprintOfProject(project, "next"));
        }


//        return modelAndView;
        sprintList = List.of(sprintService.getSprintOfProject(project, "current").get(),
                sprintService.getSprintOfProject(project, "next").get());
        return sprintList;
    }

    @PostMapping(path = "/single/{sprintId}")
    public List<Sprint> addOneTaskToSprint(@PathVariable(name = "sprintId") Long sprintId,
                                             @RequestParam(name = "task") Long taskId) {
        sprintService.addTaskToSprintById(taskId, sprintId);

        return sprintList;
    }


    @PostMapping(path = "/multiple/{sprintId}")
    public List<Sprint> addMultipleTasksToSprint(@PathVariable(name = "sprintId") Long sprintId,
                                                   @RequestBody List<Long> taskIds) {

        sprintService.addMultipleTasksToSprintById(taskIds, sprintId);

        return sprintList;
    }

}
