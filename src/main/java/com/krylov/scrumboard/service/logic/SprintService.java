package com.krylov.scrumboard.service.logic;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintList;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.repository.SprintListRepository;
import com.krylov.scrumboard.repository.SprintRepository;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.service.bean.SprintConfigurer;
import com.krylov.scrumboard.service.helper.*;
import com.krylov.scrumboard.service.request.*;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SprintService implements Runnable {

    private final SprintConfigurer sprintConfigurer;

    private final LocalDateTimeConverter converter;

    private final SprintListRepository sprintListRepository;

    private final SprintRepository sprintRepository;

    private final SprintTaskRepository sprintTaskRepository;

    public Sprint currentSprint;
    public Sprint nextSprint;
    public Duration sprintDuration;
    public SprintProperties properties;

    public SprintService(SprintConfigurer sprintConfigurer,
                         LocalDateTimeConverter converter,
                         SprintListRepository sprintListRepository,
                         SprintRepository sprintRepository,
                         SprintTaskRepository sprintTaskRepository,
                         Sprint currentSprint,
                         Sprint nextSprint,
                         Duration sprintDuration,
                         SprintProperties properties) {
        this.sprintConfigurer = sprintConfigurer;
        this.converter = converter;
        this.sprintListRepository = sprintListRepository;
        this.sprintRepository = sprintRepository;
        this.sprintTaskRepository = sprintTaskRepository;
        this.currentSprint = currentSprint;
        this.nextSprint = nextSprint;
        this.sprintDuration = sprintDuration;
        this.properties = properties;

        setSprints();

        Thread threadThis = new Thread(this);
        threadThis.start();
    }

    private void setSprints() {

        Optional<SprintList> currentOpt = sprintListRepository.findByState("current");
        if (currentOpt.isEmpty()) {
            System.out.println("DEBUG: could not find current sprint");
            return;
        }
        Optional<Sprint> sprintOptional = sprintRepository.findById(currentOpt.get().getSprintId());
        if (sprintOptional.isEmpty()) {
            System.out.println("DEBUG: could not find current sprint");
            return;
        }
        currentSprint = sprintOptional.get();


        Optional<SprintList> nextOpt = sprintListRepository.findByState("next");
        if (nextOpt.isEmpty()) {
            System.out.println("DEBUG: could not find next sprint");
            return;
        }
        sprintOptional = sprintRepository.findById(nextOpt.get().getSprintId());
        if (sprintOptional.isEmpty()) {
            System.out.println("DEBUG: could not find next sprint");
            return;
        }
        nextSprint = sprintOptional.get();

        properties = new SprintProperties(
                currentSprint.getStartOfSprint().toLocalDateTime().toLocalDate(),
                currentSprint.getEndOfSprint().toLocalDateTime().toLocalDate(),
                currentSprint.getDuration()
        );

        sprintDuration = currentSprint.getDuration();

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

        // save current and next sprints
        sprintListRepository.save(new SprintList("current", currentSprint.getId()));
        sprintListRepository.save(new SprintList("next", nextSprint.getId()));

        var sprintList = List.of(currentSprint, nextSprint);
        sprintRepository.save(currentSprint);
        sprintRepository.save(nextSprint);

        return sprintList;
    }

    @SneakyThrows
    public void run() {

        synchronized (this) {
            // if the first sprint has not ended yet
            if (currentSprint == null || LocalDateTime.now().isBefore(currentSprint.getEndOfSprint().toLocalDateTime())) {
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

            sprintListRepository.save(new SprintList("current", currentSprint.getId()));
            sprintListRepository.save(new SprintList("next", nextSprint.getId()));

            run();
        }
    }


    public void addTaskToSprintById(Long id, String state) {
        var task = findTask(id);
        if (task == null) return;

        switch (state) {
            case "current" -> task.setSprint(currentSprint);
            case "next" -> task.setSprint(nextSprint);
            default -> System.out.println("DEBUG: could not recognize state of sprint");
        }

        sprintTaskRepository.save(task);
    }

    public void addMultipleTasksToSprintById(List<Long> taskIdList, String state) {
        var list = sprintTaskRepository.findAllById(taskIdList);

        switch (state) {
            case "current" -> list.forEach(task -> task.setSprint(currentSprint));
            case "next" -> list.forEach(task -> task.setSprint(nextSprint));
            default -> System.out.println("DEBUG: could not recognize state of sprint");
        }

        sprintTaskRepository.saveAll(list);
    }


    public Optional<Sprint> getSprint(String state) {
        return switch (state) {
            case "current" -> Optional.of(currentSprint);
            case "next" -> Optional.of(nextSprint);
            default -> Optional.empty();
        };
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