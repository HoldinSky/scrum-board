package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.helper.MyDateTimeFormatter;
import com.krylov.scrumboard.helper.TaskToShow;
import com.krylov.scrumboard.repository.ProjectRepository;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.helper.Status;
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

    public Project createProject(String name) {
        var project = new Project(name);
        repository.save(project);
        return project;
    }

    public Project startProjectById(Long id, SprintRequest request) {
        var optional = repository.findById(id);
        if (optional.isEmpty()) return new Project("There are no project with such id '" + id + "'");

        var project = optional.get();
        project.setStatus(Status.IN_PROGRESS);
        List<Sprint> sprints = sprintService.configureSprint(request, project);
        sprints.forEach(project::addSprint);

        repository.save(project);
        return project;
    }

    public Project updateProject(Long id, String action) {

        return switch (action) {
            case "stop" -> stopProject(id);
            case "delete" -> deleteProject(id);
            default -> null;
        };
    }

    private Project stopProject(Long id) {
        Optional<Project> optional = repository.findById(id);
        if (optional.isEmpty()) return null;

        var project = optional.get();
        project.setStatus(Status.FINISHED);

        repository.save(project);
        sprintService.endProject(project);

        return project;
    }

    private Project deleteProject(Long id) {
        Optional<Project> optional = repository.findById(id);
        if (optional.isEmpty()) return null;

        var project = optional.get();
        if (project.getStatus() == Status.IN_PROGRESS) return null;

        repository.delete(project);
        return project;
    }

    public Project retrieveProjectByName(String name) {
        return repository.findByName(name).orElse(new Project("There are no project with such name '" + name + "'"));
    }

    public Project retrieveProjectById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Project> retrieveAllProjects() {
        return repository.findAll().stream().sorted(Comparator.comparing(p -> p.getStatus().getValue())).toList();
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

    public TaskToShow retrieveTaskById(Long projectId, Long id) {
        Optional<SprintTask> optional = backlog.findById(id);
        var sprintTask = optional.orElse(null);

        if (sprintTask == null) return null;

        var tts = new TaskToShow(
                sprintTask.getId(),
                sprintTask.getDescription(),
                MyDateTimeFormatter.formatDateTime(converter.convertToEntityAttribute(sprintTask.getCreatedAt())),
                sprintTask.getPriority());

        var projectOptional = repository.findById(projectId);
        projectOptional.ifPresent(tts::setProject);

        if (sprintTask.getDifficulty() != null) tts.setDifficulty(sprintTask.getDifficulty());
        if (sprintTask.getFinishedAt() != null)
            tts.setFinishedAt(MyDateTimeFormatter.formatDateTime(converter.convertToEntityAttribute(sprintTask.getFinishedAt())));
        if (sprintTask.getStartedAt() != null)
            tts.setStartedAt(MyDateTimeFormatter.formatDateTime(converter.convertToEntityAttribute(sprintTask.getStartedAt())));

        return tts;
    }

    public List<SprintTask> retrieveBacklog(Long projectId) {
        return backlog.retrieveBacklog(projectId);
    }

}
