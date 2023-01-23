package com.krylov.scrumboard.service.logic;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.repository.SprintRepository;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.service.bean.SprintConfigurer;
import com.krylov.scrumboard.service.helper.*;
import com.krylov.scrumboard.service.request.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SprintService implements Runnable {

    private SprintConfigurer sprintConfigurer;

    private LocalDateTimeConverter converter;

    private SprintRepository sprintRepository;

    private SprintTaskRepository sprintTaskRepository;

    private SprintServiceProps ssp;

    public List<Sprint> configureSprint(SprintRequest request) {
        // setup current sprint
        ssp.sprintDuration = Duration.valueOf(request.getSprintDuration());
        var sprintStart = request.getStartOfSprint();
        var sprintEnd = sprintStart.plusDays(ssp.sprintDuration.getDays());

        ssp.properties = new SprintProperties(
                sprintStart,
                sprintEnd,
                ssp.sprintDuration);

        sprintConfigurer.setProperties(ssp.properties);
        ssp.currentSprint = sprintConfigurer.getSprintEntity();

        // setup next sprint
        sprintStart = sprintEnd;
        sprintEnd = sprintEnd.plusDays(ssp.sprintDuration.getDays());

        ssp.properties.setStart(sprintStart);
        ssp.properties.setEnd(sprintEnd);

        sprintConfigurer.setProperties(ssp.properties);
        ssp.nextSprint = sprintConfigurer.getSprintEntity();

        var sprintList = List.of(ssp.currentSprint, ssp.nextSprint);
        sprintRepository.save(ssp.currentSprint);
        sprintRepository.save(ssp.nextSprint);

        var threadThis = new Thread(this);
        threadThis.start();

        return sprintList;
    }


    @SneakyThrows
    public void run() {

        synchronized (this) {
            // if the first sprint is not started yet
            if (LocalDateTime.now().isBefore(ssp.currentSprint.getStartOfSprint().toLocalDateTime())) {
                var tomorrow = LocalDateTime.now().plusDays(1);

                var hrs = tomorrow.getHour();
                var min = tomorrow.getMinute();
                var sec = tomorrow.getSecond();

                // wait until the midnight
                wait(86_400_000 - (hrs * 3600 + min * 60 + sec) * 1000);
                run();
            }
            wait(86_400_000L * ssp.currentSprint.getDuration().getDays());  // wait until start of next Sprint
            ssp.currentSprint = ssp.nextSprint;

            var sprintStart = LocalDate.now();
            var sprintEnd = sprintStart.plusDays(ssp.sprintDuration.getDays());

            ssp.properties.setStart(sprintStart);
            ssp.properties.setEnd(sprintEnd);

            sprintConfigurer.setProperties(ssp.properties);
            ssp.nextSprint = sprintConfigurer.getSprintEntity();

            run();
        }
    }

    public void addTaskToSprintById(Long id, Long sprintId) {
        var task = findTask(id);
        var sprint = sprintRepository.findById(sprintId).orElse(new Sprint());
        if (sprint.getId() == null) return;
        if (task == null) return;

        task.setSprint(sprint);
        sprintTaskRepository.save(task);
    }

    public void addMultipleTasksToSprintById(List<Long> taskIdList, Long sprintId) {
        var list = sprintTaskRepository.findAllById(taskIdList);
        var sprint = sprintRepository.findById(sprintId).orElse(new Sprint());
        if (sprint.getId() == null) return;


        list.forEach(task -> task.setSprint(sprint));
        sprintTaskRepository.saveAll(list);
    }


    public Sprint getSprint(Long id) {
        return sprintRepository.findById(id).orElse(new Sprint());
    }

    public List<SprintTask> retrieveBacklog() {
        return sprintTaskRepository.retrieveBacklog();
    }

    public void saveTask(SprintTaskRequest request) {
        var creationTime = LocalDateTime.now();
        var createdAt = converter.convertToDatabaseColumn(creationTime);

        var task = new SprintTask(request.getDescription(),
                createdAt,
                request.getPriority());

        if (request.getDifficulty() != null) task.setDifficulty(request.getDifficulty());

        sprintTaskRepository.save(task);
    }

    public void updateTask(Long id, String request, Byte difficulty) {
        // retrieve task from DB
        var optional = sprintTaskRepository.findById(id);
        // if there is not such task -> return
        if (optional.isEmpty()) return;

        var task = optional.get();
        var dateTime = LocalDateTime.now();

        // based on request complete actions
        switch (request) {
            case "start" -> {
                if (difficulty == null && task.getDifficulty() == null) {
                    System.out.println("DEBUG: Task must have its difficulty");
                    return;
                }
                if (task.getDifficulty() == null) task.setDifficulty(difficulty);
                task.setStartedAt(converter.convertToDatabaseColumn(dateTime));
            }
            case "finish" -> {
                if (task.getStartedAt() == null) {
                    System.out.println("DEBUG: Task was not started yet");
                    return;
                }
                task.setFinishedAt(converter.convertToDatabaseColumn(dateTime));
            }
            case "setDifficulty" -> {
                if (difficulty == 0) {
                    System.out.println("DEBUG: Task must have its difficulty");
                    return;
                }
                task.setDifficulty(difficulty);
            }
            default -> {
                System.out.println("DEBUG: Cannot recognize update request \"" + request + "\"");
            }
        }

        // update DB instance
        sprintTaskRepository.save(task);
    }

    public void deleteTask(Long id) {
        // retrieve task from DB
        var optional = sprintRepository.findById(id);
        // if there is not such task -> return
        if (optional.isEmpty()) return;

        var task = optional.get();
        sprintRepository.delete(task);
    }

    private SprintTask findTask(Long id) {
        var taskOptional = sprintTaskRepository.findById(id);
        if (taskOptional.isEmpty()) return null;

        return taskOptional.get();
    }

}