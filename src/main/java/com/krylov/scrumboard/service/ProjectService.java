package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.repository.ProjectRepository;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.helper.Status;
import com.krylov.scrumboard.request.SprintRequest;
import com.krylov.scrumboard.request.SprintTaskRequest;
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

    public Project createProject(String name) {
        var project = new Project(name);
        repository.save(project);
        return project;
    }

    public Project startProjectByName(String name, SprintRequest request) {
        var optional = repository.findByName(name);
        if (optional.isEmpty()) return new Project("There are no project with such name '" + name + "'");

        var project = optional.get();
        project.setStatus(Status.IN_PROGRESS);
        List<Sprint> sprints = sprintService.configureSprint(request, project);
        sprints.forEach(project::addSprint);

        repository.save(project);
        return project;
    }

    public Project stopProject(String name) {
        Optional<Project> optional = repository.findByName(name);
        if (optional.isEmpty()) return new Project("There are no project with such name '" + name + "'");

        var project = optional.get();
        project.setStatus(Status.FINISHED);

        repository.save(project);
        sprintService.endProject(project);

        return project;
    }

    public Project deleteProject(String name) {
        Optional<Project> optional = repository.findByName(name);
        if (optional.isEmpty()) return new Project("There are no project with such name '" + name + "'");

        var project = optional.get();
        if (project.getStatus() == Status.IN_PROGRESS) return new Project("Started project cannot be deleted!");

        repository.delete(project);
        return project;
    }

    public Project retrieveProjectByName(String name) {
        return repository.findByName(name).orElse(new Project("There are no project with such name '" + name + "'"));
    }

    public Project retrieveProjectById(Long id) {
        return repository.findById(id).orElse(new Project("There are no project with such id '" + id + "'"));
    }

    public List<Project> retrieveAllProjects() {
        return repository.findAll().stream().sorted(Comparator.comparing(p -> p.getStatus().getValue())).toList();
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

    public List<SprintTask> retrieveBacklog(String projectName) {
        return backlog.retrieveBacklog(projectName);
    }

}
