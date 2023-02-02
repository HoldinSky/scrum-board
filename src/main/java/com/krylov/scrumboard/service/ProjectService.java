package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.enums.Status;
import com.krylov.scrumboard.helper.*;
import com.krylov.scrumboard.repository.ProjectRepository;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.request.SprintRequest;
import com.krylov.scrumboard.request.TaskRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final LocalDateTimeConverter converter;
    private final ProjectRepository repository;
    private final SprintTaskRepository backlog;
    private final SprintService sprintService;

    public ProjectService(LocalDateTimeConverter converter,
                          ProjectRepository repository,
                          SprintService service,
                          SprintTaskRepository taskRepository) {
        this.repository = repository;
        this.sprintService = service;
        this.backlog = taskRepository;
        this.converter = converter;
    }

    public void createProject(String name) {
        var project = new Project(name);
        repository.save(project);
    }

    public void startProjectById(Long id, SprintRequest request) {
        var optional = repository.findById(id);
        if (optional.isEmpty()) return;

        var project = optional.get();
        project.setStatus(Status.IN_PROGRESS);

        sprintService.configureSprint(request, project);
        repository.save(project);
    }


    public void stopProject(Long id) {
        Optional<Project> optional = repository.findById(id);
        if (optional.isEmpty()) return;

        var project = optional.get();
        project.setStatus(Status.FINISHED);

        repository.save(project);
        sprintService.endProject(project);
    }

    public void deleteProject(Long id) {
        Optional<Project> optional = repository.findById(id);
        if (optional.isEmpty()) return;

        var project = optional.get();
        if (project.getStatus() == Status.IN_PROGRESS) return;

        repository.delete(project);
    }

    public Project retrieveProjectById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Project> retrieveAllProjects() {
        return repository.findAll()
                .stream().sorted(Comparator.comparing(p -> p.getStatus().getValue())).toList();
    }

    public Sprint retrieveCurrentSprintById(Long id) {
        return sprintService.getSprintOfProject(id, "current");
    }

    public Sprint retrieveNextSprintById(Long id) {
        return sprintService.getSprintOfProject(id, "next");
    }

    public List<SprintToShow> retrieveAllSprintsById(Long id) {
        return sprintService.getAllSprintsOfProject(id);
    }

    public void saveTask(TaskRequest request, Long id) {
        var creationTime = LocalDateTime.now();
        var createdAt = converter.convertToDatabaseColumn(creationTime);

        var optional = repository.findById(id);
        if (optional.isEmpty()) return;

        var task = new SprintTask(request.getDescription(),
                createdAt,
                request.getPriority(),
                optional.get());

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
            default ->
                System.out.println("DEBUG: Cannot recognize update request \"" + request + "\"");
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

    public List<SprintTask> retrieveBacklog(Long projectId) {
        return backlog.retrieveBacklog(projectId)
                .stream().sorted(
                        Comparator.comparing(SprintTask::getId)
                        .thenComparing(SprintTask::getPriority)).toList();
    }

}
