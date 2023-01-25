package com.krylov.scrumboard.service.logic;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.repository.ProjectRepository;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.service.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.service.request.SprintRequest;
import com.krylov.scrumboard.service.request.SprintTaskRequest;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService implements Runnable {

    private final LocalDateTimeConverter converter;

    private final ProjectRepository repository;

    private final SprintTaskRepository backlog;
    private final SprintService sprintService;

    private volatile boolean inProgress;

    public ProjectService(LocalDateTimeConverter converter,
                          ProjectRepository repository,
                          SprintService service,
                          SprintTaskRepository taskRepository) {
        this.repository = repository;
        this.sprintService = service;
        this.backlog = taskRepository;
        this.converter = converter;

        inProgress = false;
    }

    @Override
    @SneakyThrows
    public void run() {
        synchronized (this) {
            if (!inProgress) return;
            wait(86_400_000L * 7);
            run();
        }
    }

    public Project createProject(String name) {
        var project = new Project(name);
        repository.save(project);
        inProgress = true;
        return project;
    }

    public Project startProjectByName(String name, SprintRequest request) {
        var optional = repository.findByName(name);
        if (optional.isEmpty()) return new Project("There are no project with such name '" + name + "'");

        var project = optional.get();
        List<Sprint> sprints = sprintService.configureSprint(request, project);
        sprints.forEach(project::addSprint);

        repository.save(project);
        return project;
    }

    public Project stopProject(String name) {
        inProgress = false;
        return repository.findByName(name).orElse(new Project("There are no project with such name '" + name + "'"));
    }

    public void saveTask(SprintTaskRequest request) {
        var creationTime = LocalDateTime.now();
        var createdAt = converter.convertToDatabaseColumn(creationTime);

        var task = new SprintTask(request.getDescription(),
                createdAt,
                request.getPriority());

        if (request.getDifficulty() != null) task.setDifficulty(request.getDifficulty());

        backlog.save(task);
    }

    public void updateTask(Long id, String request, Byte difficulty) {
        // retrieve task from DB
        var optional = backlog.findById(id);
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
        backlog.save(task);
    }

    public void deleteTask(Long id) {
        // retrieve task from DB
        var optional = backlog.findById(id);
        // if there is not such task -> return
        if (optional.isEmpty()) return;

        var task = optional.get();
        backlog.delete(task);
    }

    public List<SprintTask> retrieveBacklog() {
        return backlog.retrieveBacklog();
    }

}
