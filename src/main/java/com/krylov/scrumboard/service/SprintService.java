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
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
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

    public void addMultipleTasksToSprintById(List<Long> taskIdList, Long sprintId) {
        var list = sprintTaskRepo.findAllById(taskIdList);

        Optional<Sprint> sprintOpt = sprintRepo.findById(sprintId);
        if (sprintOpt.isEmpty()) {
            System.out.println("DEBUG: Could not find sprint by id '" + sprintId + "'");
            return;
        }
        Sprint sprint = sprintOpt.get();
        list.forEach(task -> task.setSprint(sprint));

        sprintTaskRepo.saveAll(list);
    }

    public TaskToShow getSprintTask(Long id) {
        var task = sprintTaskRepo.findById(id).orElse(null);
        if (task == null) return null;

        TaskToShow toShow = new TaskToShow(
                task.getId(),
                task.getDescription(),
                MyDateTimeFormatter.formatDateTime(task.getCreatedAt().toLocalDateTime()),
                task.getPriority());

        toShow.setDifficulty(task.getDifficulty());
        toShow.setProject(task.getProject());
        toShow.setSprint(task.getSprint());

        if (task.getStartedAt() != null)
            toShow.setStartedAt(MyDateTimeFormatter.formatDateTime(task.getStartedAt().toLocalDateTime()));
        if (task.getFinishedAt() != null)
            toShow.setFinishedAt(MyDateTimeFormatter.formatDateTime(task.getFinishedAt().toLocalDateTime()));

        return toShow;
    }

    public List<SprintTask> getTasksOfSprint(Long id) {
        return sprintTaskRepo.retrieveTasksOfSprintById(id)
                .stream().sorted(Comparator.comparing(SprintTask::getPriority)).toList();
    }

    public void startTaskById(Long id) {
        var task = findTask(id);
        if (task == null) return;

        if (task.getDifficulty() == null) return;

        task.setStartedAt(converter.convertToDatabaseColumn(LocalDateTime.now()));
        sprintTaskRepo.save(task);
    }

    public void setDifficultyToTask(Long id, Byte difficulty) {
        var task = findTask(id);
        if (task == null) return;

        task.setDifficulty(difficulty);
        sprintTaskRepo.save(task);
    }

    public void finishTask(Long id) {
        var task = findTask(id);
        if (task == null) return;

        task.setFinishedAt(converter.convertToDatabaseColumn(LocalDateTime.now()));
        sprintTaskRepo.save(task);
    }

    public void deleteTask(Long id) {
        sprintTaskRepo.deleteById(id);
    }


    public List<SprintToShow> getAllSprintsOfProject(Long id) {
        Optional<Project> projectOptional = projectRepo.findById(id);
        if (projectOptional.isEmpty()) return null;

        return sprintRepo.findByProject(projectOptional.get())
                .stream().map(s -> new SprintToShow(
                        s.getId(),
                        s.getStartOfSprint().toLocalDateTime().toLocalDate().toString(),
                        s.getEndOfSprint().toLocalDateTime().toLocalDate().toString(),
                        s.getProject())).toList();
    }

    public Sprint getSprintOfProject(Long id, String state) {
        Optional<Project> projectOptional = projectRepo.findById(id);
        if (projectOptional.isEmpty()) return null;

        return switch (state) {
            case "current" ->
                this.current.stream()
                        .filter(sprint -> sprint.getProject().getId().equals(id))
                        .sorted(sprintComparator).iterator().next();
            case "next" ->
                this.next.stream()
                        .filter(sprint -> sprint.getProject().getId().equals(id))
                        .sorted(sprintComparator).iterator().next();
            default -> null;
        };
    }

    public SprintToShow getSprintToShowOfProject(Long id, String state) {

        Optional<Project> projectOptional = projectRepo.findById(id);
        if (projectOptional.isEmpty()) return null;

        SprintToShow toShow;
        switch (state) {
            case "current" -> {
                Sprint current = this.current.stream()
                        .filter(sprint -> sprint.getProject().getId().equals(id))
                        .sorted(sprintComparator).iterator().next();

                toShow = new SprintToShow(
                        current.getId(),
                        MyDateTimeFormatter.formatDate(current.getStartOfSprint().toLocalDateTime()),
                        MyDateTimeFormatter.formatDate(current.getEndOfSprint().toLocalDateTime()),
                        current.getProject());
            }
            case "next" -> {
                Sprint next = this.next.stream()
                        .filter(sprint -> sprint.getProject().getId().equals(id))
                        .sorted(sprintComparator).iterator().next();

                toShow = new SprintToShow(next.getId(),
                        MyDateTimeFormatter.formatDate(next.getStartOfSprint().toLocalDateTime()),
                        MyDateTimeFormatter.formatDate(next.getEndOfSprint().toLocalDateTime()),
                        next.getProject());
            }
            default -> toShow = null;
        }

        return toShow;
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

    private SprintTask findTask(Long id) {
        var taskOptional = sprintTaskRepo.findById(id);
        if (taskOptional.isEmpty()) return null;

        return taskOptional.get();
    }

}