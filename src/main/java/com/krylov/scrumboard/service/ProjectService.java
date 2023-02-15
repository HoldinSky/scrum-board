package com.krylov.scrumboard.service;

import com.krylov.scrumboard.request.UpdateTaskRequest;
import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.enums.Status;
import com.krylov.scrumboard.helper.*;
import com.krylov.scrumboard.repository.ProjectRepository;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.request.SprintRequest;
import com.krylov.scrumboard.request.TaskRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@Transactional
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

    public Project startProjectById(Long id, SprintRequest request) {
        try {
            var project = repository.findById(id).orElseThrow(() -> new RuntimeException("Project is not found in database with id: " + id));
            project.setStatus(Status.IN_PROGRESS);

            sprintService.configureSprint(request, project);
            return project;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }
//        repository.save(project);
    }


    public Project stopProject(Long id) {
        try {
            Project project = repository.findById(id).orElseThrow(() -> new RuntimeException("Project is not found in database with id: " + id));

            project.setStatus(Status.FINISHED);
            repository.save(project);
            sprintService.endProject(project);
            return project;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public ProjectOrError deleteProject(Long id) {
        try {
            Project project = repository.findById(id).orElseThrow(() -> new RuntimeException("Project is not found in database with id: " + id));
            if (project.getStatus() == Status.IN_PROGRESS) return new ProjectOrError(project, "You cannot delete started project!");
            repository.delete(project);
            return new ProjectOrError(project, null);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return new ProjectOrError(null, exception.getMessage());
        }
    }

    public Project getProjectById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Project> getAllProjects() {
        return repository.findAll()
                .stream().sorted(Comparator.comparing(p -> p.getStatus().getValue())).toList();
    }

    public Sprint getCurrentSprintById(Long id) {
        return sprintService.getSprintOfProject(id, "current");
    }

    public Sprint getNextSprintById(Long id) {
        return sprintService.getSprintOfProject(id, "next");
    }

    public List<Sprint> getAllSprintsById(Long id) {
        return sprintService.getAllSprintsOfProject(id);
    }

    public void saveTask(TaskRequest request, Long id) {
        try {
            var project = repository.findById(id).orElseThrow(() -> new RuntimeException("Project is not found in database with id: " + id));
            var createdAt = converter.convertToDatabaseColumn(LocalDateTime.now());

            var task = new SprintTask(request.getDescription(),
                    createdAt,
                    request.getPriority(),
                    project);

            if (request.getDifficulty() != null) task.setDifficulty(request.getDifficulty());

            backlog.save(task);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
        }
    }

    public void updateTask(Long id, UpdateTaskRequest request) {
        try {
            // retrieve task from DB
            var task = backlog.findById(id).orElseThrow(() -> new RuntimeException("Task is not found in database with id: " + id));

            var dateTime = LocalDateTime.now();

            String action = request.getAction();
            Byte difficulty = request.getDifficulty();

            // based on request complete actions
            switch (action) {
                case "start" -> {
                    if (difficulty == null && task.getDifficulty() == null) {
                        log.error("Task must have its difficulty");
                        return;
                    }
                    if (task.getDifficulty() == null) task.setDifficulty(difficulty);
                    task.setStartedAt(converter.convertToDatabaseColumn(dateTime));
                }
                case "finish" -> {
                    if (task.getStartedAt() == null) {
                        log.error("Task was not started yet");
                        return;
                    }
                    task.setFinishedAt(converter.convertToDatabaseColumn(dateTime));
                }
                case "Set difficulty" -> {
                    if (difficulty == 0) {
                        log.error("Task must have its difficulty");
                        return;
                    }
                    task.setDifficulty(difficulty);
                }
                default -> log.error("Cannot recognize update request \"" + request + "\"");
            }

//             update DB instance
//            backlog.save(task);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
        }
    }

    public void deleteTask(Long id) {
        backlog.deleteById(id);
    }

    public List<SprintTask> getBacklog(Long projectId) {
        return backlog.retrieveBacklog(projectId)
                .stream().sorted(
                        Comparator.comparing(SprintTask::getId)
                                .thenComparing(SprintTask::getPriority)).toList();
    }

}
