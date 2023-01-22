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

@Service
@AllArgsConstructor
public class SprintService implements Runnable {

    private SprintConfigurer sprintConfigurer;

    private LocalDateTimeConverter converter;

    private SprintRepository sprintRepository;

    private SprintTaskRepository sprintTaskRepository;

    private Sprint currentSprint;

    private Sprint nextSprint;

    private Duration sprintDuration;

    private SprintProperties properties;

    public SprintService() {
        sprintConfigurer = new SprintConfigurer();
        converter = new LocalDateTimeConverter();

        sprintConfigurer.setConverter(converter);
    }

    public List<Sprint> configureSprint(SprintRequest request) {
        // setup current sprint
        sprintDuration = Duration.valueOf(request.getSprintDuration());
        var sprintStart = request.getStartOfSprint();
        var sprintEnd = sprintStart.plusDays(sprintDuration.getDays());

        properties = new SprintProperties(
                sprintStart,
                sprintEnd,
                sprintDuration);

        sprintConfigurer.setProperties(properties);
        currentSprint = sprintConfigurer.getSprintEntity();

        // setup next sprint
        sprintStart = sprintEnd;
        sprintEnd = sprintEnd.plusDays(sprintDuration.getDays());

        properties.setStart(sprintStart);
        properties.setEnd(sprintEnd);

        sprintConfigurer.setProperties(properties);
        nextSprint = sprintConfigurer.getSprintEntity();

        var sprintList = List.of(currentSprint, nextSprint);
        sprintRepository.saveAll(sprintList);

        var threadThis = new Thread(this);
        threadThis.start();

        return sprintList;
    }


    @SneakyThrows
    public void run() {

        synchronized (this) {
            // if the first sprint is not started yet
            if (LocalDateTime.now().isBefore(currentSprint.getStartOfSprint().toLocalDateTime())) {
                var tomorrow = LocalDateTime.now().plusDays(1);

                var hrs = tomorrow.getHour();
                var min = tomorrow.getMinute();
                var sec = tomorrow.getSecond();

                // wait until the midnight
                wait(86_400_000 - (hrs * 3600 + min * 60 + sec) * 1000);
                run();
            }
            wait(86_400_000L * currentSprint.getDuration().getDays());  // wait until start of next Sprint
            currentSprint = nextSprint;

            var sprintStart = LocalDate.now();
            var sprintEnd = sprintStart.plusDays(sprintDuration.getDays());

            properties.setStart(sprintStart);
            properties.setEnd(sprintEnd);

            sprintConfigurer.setProperties(properties);
            nextSprint = sprintConfigurer.getSprintEntity();

            run();
        }
    }

    public void addTaskToSprintById(Long id, String sprintVariant) {
        var task = findTask(id);

        switch (sprintVariant) {
            case "current" -> {
                currentSprint.addSprintTask(task);
                sprintRepository.save(currentSprint);
            }
            case "next" -> {
                nextSprint.addSprintTask(task);
                sprintRepository.save(nextSprint);
            }
            default -> System.out.println("DEBUG: cannot recognize sprint variant");
        }
    }

    public void addMultipleTasksToSprintById(List<Long> taskIdList, String sprintVariant) {
        var list = sprintTaskRepository.findAllById(taskIdList);

        switch (sprintVariant) {
            case "current" -> {
                list.forEach(task -> currentSprint.addSprintTask(task));
                sprintRepository.save(currentSprint);
            }
            case "next" -> {
                list.forEach(task -> nextSprint.addSprintTask(task));
                sprintRepository.save(nextSprint);
            }
            default -> System.out.println("DEBUG: cannot recognize sprint variant");
        }
    }

    public Sprint getSprint(String variant) {
        return switch (variant) {
            case "current" -> currentSprint;
            case "next" -> nextSprint;
            default -> null;
        };
    }

    public List<SprintTask> retrieveBacklog() {
        return sprintTaskRepository.findAll();
    }

    public void addTaskToBacklog(SprintTaskRequest request) {
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

    private SprintTask findTask(Long id) {
        var taskOptional = sprintTaskRepository.findById(id);
        if (taskOptional.isEmpty()) return null;

        return taskOptional.get();
    }

}