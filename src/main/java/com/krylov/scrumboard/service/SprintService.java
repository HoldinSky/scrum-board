package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.enums.Duration;
import com.krylov.scrumboard.enums.Status;
import com.krylov.scrumboard.helper.*;
import com.krylov.scrumboard.repository.ProjectRepository;
import com.krylov.scrumboard.repository.SprintRepository;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.request.SprintRequest;
import com.krylov.scrumboard.request.UpdateTaskRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class SprintService implements Runnable {

    private final LocalDateTimeConverter converter;
    private final SprintRepository sprintRepo;
    private final SprintTaskRepository sprintTaskRepo;
    private final ProjectRepository projectRepo;
    private final LinkedSet<Sprint> current;
    private final LinkedSet<Sprint> next;
    private Thread thread;
    private final AtomicBoolean running;
    private final Comparator<Sprint> sprintComparator;

    @SneakyThrows
    public SprintService(LocalDateTimeConverter converter,
                         SprintRepository sprintRepo,
                         SprintTaskRepository sprintTaskRepo,
                         ProjectRepository projectRepo) {

        this.converter = converter;
        sprintComparator = Comparator.comparing(Sprint::getStartOfSprint, Comparator.reverseOrder());

        this.sprintRepo = sprintRepo;
        this.sprintTaskRepo = sprintTaskRepo;
        this.projectRepo = projectRepo;

        this.current = new LinkedSet<>();
        this.next = new LinkedSet<>();

        running = new AtomicBoolean(false);

        setSprints();
    }

    private Sprint mapSprintDtoToSprint(SprintRepository.SprintDTO sDTO) {

        Sprint sprint = new Sprint();
        sprint.setStartOfSprint(sDTO.getStart());
        sprint.setEndOfSprint(sDTO.getFinish());
        sprint.setDuration(Duration.valueOf(sDTO.getDuration()));
        sprint.setId(sDTO.getId());

        sprint.setProject(mapSprintDtoToProject(sDTO));

        return sprint;
    }

    private Project mapSprintDtoToProject(SprintRepository.SprintDTO sDTO) {
        Project project = new Project();
        project.setId(sDTO.getProjectId());
        project.setName(sDTO.getName());
        project.setStatus(Status.valueOf(sDTO.getStatus()));

        return project;
    }

    // !! HEAVY METHOD !! USE ONLY ON RELOAD !!
    private void setSprints() {

        List<SprintRepository.SprintDTO> sprintDTOS = sprintRepo.findAllActiveSprints();

        if (sprintDTOS.size() == 0) return;

        List<Sprint> sprintInProgress = sprintDTOS.stream().map(this::mapSprintDtoToSprint).toList();
        List<Project> projectInProgress = new ArrayList<>(
                new HashSet<>(sprintDTOS.stream().map(this::mapSprintDtoToProject).toList()));

        projectInProgress.forEach(project -> {
            List<Sprint> sprints = sprintInProgress.stream()
                    .filter(s -> s.getProject().getId().equals(project.getId())).sorted(sprintComparator).toList();
            if (sprints.size() >= 2) {
                next.add(sprints.get(0));
                current.add(sprints.get(1));
            }
        });

        if (!current.isEmpty()) {
            running.set(true);
        }

        if (running.get()) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void configureSprint(SprintRequest request, Project project) {

        // setup current sprint
        var duration = Duration.valueOf(request.getSprintDuration());
        var sprintStart = request.getStartOfSprint();
        var sprintEnd = sprintStart.plusDays(duration.getDays());

        var sprint = new Sprint(
                converter.convertToDatabaseColumn(sprintStart.atStartOfDay()),
                converter.convertToDatabaseColumn(sprintEnd.atStartOfDay()),
                duration
        );
        sprint.setProject(project);

        // persist current sprint
        current.add(sprint);
        sprintRepo.save(sprint);

        // setup next sprint
        sprintStart = sprintEnd;
        sprintEnd = sprintStart.plusDays(duration.getDays());

        sprint = new Sprint(
                converter.convertToDatabaseColumn(sprintStart.atStartOfDay()),
                converter.convertToDatabaseColumn(sprintEnd.atStartOfDay()),
                duration
        );
        sprint.setProject(project);

        // persist next sprint
        next.add(sprint);
        sprintRepo.save(sprint);

        // start sprint updater thread

        if (!running.get()) {
            running.set(true);
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(this);
                thread.start();
            }
        }
    }

    @SneakyThrows
    public void run() {

        while (running.get()) {
            synchronized (current) {

                current.forEach(sprint -> {
                    if (LocalDateTime.now().isAfter(sprint.getEndOfSprint().toLocalDateTime())) current.remove(sprint);
                });

                next.forEach(sprint -> {
                    if (LocalDateTime.now().isAfter(sprint.getStartOfSprint().toLocalDateTime())) {
                        // reconfiguring sprints that have to be started
                        // add sprint to current, if its start date has passed
                        current.add(sprint);
                        var duration = sprint.getDuration();

                        // describe new 'next' sprint
                        var nextSprint = new Sprint(
                                sprint.getEndOfSprint(),
                                converter.convertToDatabaseColumn(sprint
                                        .getEndOfSprint().toLocalDateTime().plusDays(duration.getDays())),
                                duration
                        );

                        // map sprint to the same project and save it
                        nextSprint.setProject(sprint.getProject());
                        sprintRepo.save(nextSprint);

                        next.add(nextSprint);
                        next.remove(sprint);
                    }
                });
            }
            synchronized (this) {
                var tomorrow = LocalDateTime.now().plusDays(1);

                var hrs = tomorrow.getHour();
                var min = tomorrow.getMinute();
                var sec = tomorrow.getSecond();

                // wait until the midnight
                this.wait((86_400 - (hrs * 3600 + min * 60 + sec)) * 1000);
            }
        }
    }

    public Sprint addMultipleTasksToSprintById(Long[] taskIdList, Long sprintId) {
        var list = sprintTaskRepo.findAllById(Arrays.stream(taskIdList).toList());

        Optional<Sprint> sprintOpt = sprintRepo.findById(sprintId);
        if (sprintOpt.isEmpty()) {
            log.error("Could not find sprint by id '" + sprintId + "'");
            return null;
        }
        Sprint sprint = sprintOpt.get();
        list.forEach(task -> task.setSprint(sprint));

        sprintTaskRepo.saveAll(list);
        return sprint;
    }

    public SprintTask getSprintTask(Long id) {
        try {
            return sprintTaskRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Task is not found in database with id: " + id));
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public List<SprintTask> getTasksOfSprint(Long id) {
        return sprintTaskRepo.retrieveTasksOfSprintById(id)
                .stream().sorted(Comparator.comparing(SprintTask::getPriority)).toList();
    }

    public List<SprintTask> getBacklogOfSprint(Long id) {
        return sprintTaskRepo.retrievePlannedSprintTask(id)
                .stream().sorted(Comparator.comparing(SprintTask::getPriority)).toList();
    }

    public List<SprintTask> getInProgressOfSprint(Long id) {
        return sprintTaskRepo.retrieveInProgressSprintTask(id)
                .stream().sorted(Comparator.comparing(SprintTask::getPriority)).toList();
    }

    public List<SprintTask> getFinishedOfSprint(Long id) {
        return sprintTaskRepo.retrieveFinishedSprintTask(id)
                .stream().sorted(Comparator.comparing(SprintTask::getPriority)).toList();
    }

    public SprintTaskOrError updateTaskById(Long id, UpdateTaskRequest request) {
        try {
            // retrieve task from DB
            var task = sprintTaskRepo.findById(id).orElseThrow(() -> new RuntimeException("Task is not found in database with id: " + id));

            var dateTime = LocalDateTime.now();

            String action = request.getAction();
            Byte difficulty = request.getDifficulty();

            // based on request complete actions
            switch (action) {
                case "start" -> {
                    if (difficulty == null && task.getDifficulty() == null) {
                        log.error("Task must have its difficulty");
                        return new SprintTaskOrError(null, "Task must have its difficulty");
                    }
                    if (task.getDifficulty() == null) setDifficultyToTask(task, difficulty);
                    startTask(task);
                }
                case "finish" -> {
                    if (task.getStartedAt() == null) {
                        log.error("Task was not started yet");
                        return new SprintTaskOrError(null, "Task was not started yet");
                    }
                    task.setFinishedAt(converter.convertToDatabaseColumn(dateTime));
                }
                case "Set difficulty" -> {
                    if (difficulty == 0) {
                        log.error("Task must have its difficulty");
                        return new SprintTaskOrError(null, "Task must have its difficulty");
                    }
                    finishTask(task);
                }
                default -> {
                    log.error("Cannot recognize update request \"" + request + "\"");
                    return new SprintTaskOrError(null, "Cannot recognize update request \"" + request + "\"");
                }
            }

//             update DB instance
            return new SprintTaskOrError(task, null);
//            backlog.save(task);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return new SprintTaskOrError(null, exception.getMessage());
        }
    }


    private void startTask(SprintTask task) {
        task.setStartedAt(converter.convertToDatabaseColumn(LocalDateTime.now()));
    }

    private void setDifficultyToTask(SprintTask task, Byte difficulty) {
        task.setDifficulty(difficulty);
    }

    private void finishTask(SprintTask task) {
        task.setFinishedAt(converter.convertToDatabaseColumn(LocalDateTime.now()));
    }

    public SprintTask deleteTask(Long id) {
        SprintTask task = sprintTaskRepo.findById(id).orElse(null);
        sprintTaskRepo.deleteById(id);
        return task;
    }


    public List<Sprint> getAllSprintsOfProject(Long id) {
        Optional<Project> projectOptional = projectRepo.findById(id);
        if (projectOptional.isEmpty()) return null;

        return sprintRepo.findByProject(projectOptional.get());
    }

    public Sprint getSprintOfProject(Long id, String state) {
        Optional<Project> projectOptional = projectRepo.findById(id);
        if (projectOptional.isEmpty()) return null;

        return switch (state) {
            case "current" -> this.current.stream()
                    .filter(sprint -> sprint.getProject().getId().equals(id))
                    .sorted(sprintComparator).iterator().next();
            case "next" -> this.next.stream()
                    .filter(sprint -> sprint.getProject().getId().equals(id))
                    .sorted(sprintComparator).iterator().next();
            default -> null;
        };
    }

    public void endProject(Project project) {

        // free list of sprints
        for (Sprint s : current) {
            if (s.getProject().getId().equals(project.getId())) {
                current.remove(s);
                break;
            }
        }

        for (Sprint s : next) {
            if (s.getProject().getId().equals(project.getId())) {
                next.remove(s);
                break;
            }
        }

        if (current.isEmpty()) running.set(false);
    }

}