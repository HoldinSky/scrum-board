package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.helper.Duration;
import com.krylov.scrumboard.helper.LinkedSet;
import com.krylov.scrumboard.helper.SprintProperties;
import com.krylov.scrumboard.repository.ProjectRepository;
import com.krylov.scrumboard.repository.SprintRepository;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.bean.SprintConfigurer;
import com.krylov.scrumboard.request.SprintRequest;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SprintService implements Runnable {

    private final SprintConfigurer sprintConfigurer;

    private final SprintRepository sprintRepo;

    private final SprintTaskRepository sprintTaskRepo;

    private final ProjectRepository projectRepo;

    private final LinkedSet<Sprint> current;
    private final LinkedSet<Sprint> next;
    private Duration sprintDuration;
    private final ExecutorService service;
    private final AtomicBoolean running;
    private final Comparator<Sprint> sprintComparator;

    @SneakyThrows
    public SprintService(SprintConfigurer sprintConfigurer,
                         SprintRepository sprintRepo,
                         SprintTaskRepository sprintTaskRepo,
                         ProjectRepository projectRepo) {
        this.sprintConfigurer = sprintConfigurer;
        sprintComparator = Comparator.comparing(Sprint::getStartOfSprint, Comparator.reverseOrder());

        this.sprintRepo = sprintRepo;
        this.sprintTaskRepo = sprintTaskRepo;
        this.projectRepo = projectRepo;

        this.current = new LinkedSet<>();
        this.next = new LinkedSet<>();

        this.sprintDuration = Duration.NONE;

        running = new AtomicBoolean(false);

        int cores = Runtime.getRuntime().availableProcessors();
        service = Executors.newFixedThreadPool(cores);

        setSprints();
    }

    private void setSprints() {

        List<Sprint> sprintInProgress = sprintRepo.findAllActiveSprints();
        List<Project> projectInProgress = projectRepo.findAllActiveProjects();

        if (sprintInProgress == null) return;
        projectInProgress.forEach(project -> {
            List<Sprint> sprints = sprintInProgress.stream()
                    .filter(s -> Objects.equals(s.getProject().getId(), project.getId())).sorted(sprintComparator).toList();
            if (sprints.size() >= 2) {
                next.add(sprints.get(0));
                current.add(sprints.get(1));
            }
        });

        if (!current.isEmpty()) {
            sprintDuration = current.iterator().next().getDuration();
            running.set(true);
        }

        if (running.get()) {
            service.execute(this);
        }
    }

    public List<Sprint> configureSprint(SprintRequest request, Project project) {

        // setup current sprint
        sprintDuration = Duration.valueOf(request.getSprintDuration());
        var sprintStart = request.getStartOfSprint();
        var sprintEnd = sprintStart.plusDays(sprintDuration.getDays());

        var properties = new SprintProperties(
                sprintStart,
                sprintEnd,
                sprintDuration);

        sprintConfigurer.setProperties(properties);
        var sprint = sprintConfigurer.getSprintEntity();
        sprint.setProject(project);
        current.add(sprint);
        sprintRepo.save(sprint);

        // setup next sprint
        sprintStart = sprintEnd;
        sprintEnd = sprintEnd.plusDays(sprintDuration.getDays());

        properties.setStart(sprintStart);
        properties.setEnd(sprintEnd);

        sprintConfigurer.setProperties(properties);
        sprint = sprintConfigurer.getSprintEntity();
        sprint.setProject(project);
        next.add(sprint);

        sprintRepo.save(sprint);

        running.set(true);
        service.execute(this);

        List<Sprint> toRet = new ArrayList<>(current);
        toRet.addAll(next);
        return toRet;
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
                        // add to current sprint, which start date has passed
                        current.add(sprint);

                        // describe new 'next' sprint
                        var properties = new SprintProperties(
                                sprint.getEndOfSprint().toLocalDateTime().toLocalDate(),
                                sprint.getEndOfSprint().toLocalDateTime().toLocalDate().plusDays(sprintDuration.getDays()),
                                sprintDuration
                        );
                        sprintConfigurer.setProperties(properties);
                        Sprint sprintEntity = sprintConfigurer.getSprintEntity();

                        // map sprint to the same project and save it
                        sprintEntity.setProject(sprint.getProject());
                        sprintRepo.save(sprintEntity);

                        next.add(sprintEntity);
                        next.remove(sprint);
                    }
                });


            }
            synchronized (service) {
                var tomorrow = LocalDateTime.now().plusDays(1);

                var hrs = tomorrow.getHour();
                var min = tomorrow.getMinute();
                var sec = tomorrow.getSecond();

                // wait until the midnight
                service.wait((86_400 - (hrs * 3600 + min * 60 + sec)) * 1000);
            }
        }
    }

    public void addTaskToSprintById(Long taskId, Long sprintId) {
        var task = findTask(taskId);
        if (task == null) return;

        Optional<Sprint> sprintOpt = sprintRepo.findById(sprintId);
        if (sprintOpt.isEmpty()) {
            System.out.println("DEBUG: Could not find sprint by id '" + sprintId + "'");
            return;
        }
        Sprint sprint = sprintOpt.get();
        task.setSprint(sprint);

        sprintTaskRepo.save(task);
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

    public Sprint getSprintById(Long id) {
        Optional<Sprint> sprintOptional = sprintRepo.findById(id);
        if (sprintOptional.isEmpty()) return null;

        return sprintOptional.get();
    }

    public Sprint getSprintOfProject(String name, String state) {

        Optional<Project> projectOptional = projectRepo.findByName(name);
        if (projectOptional.isEmpty()) return null;
        Project project = projectOptional.get();

        return switch (state) {
            case "current" -> current.stream()
                    .filter(sprint -> sprint.getProject().equals(project))
                    .sorted().toList().get(0);
            case "next" -> next.stream()
                    .filter(sprint -> sprint.getProject().equals(project))
                    .sorted().toList().get(0);
            default -> null;
        };
    }

    public List<SprintTask> retrieveTaskOfSprintOfProject(String name, String state) {

        Optional<Project> projectOptional = projectRepo.findByName(name);
        if (projectOptional.isEmpty()) return new ArrayList<>();
        Project project = projectOptional.get();

        return switch (state) {
            case "current" -> current.stream()
                    .filter(sprint -> sprint.getProject().equals(project))
                    .sorted().toList().get(0).getTaskList();
            case "next" -> next.stream()
                    .filter(sprint -> sprint.getProject().equals(project))
                    .sorted().toList().get(0).getTaskList();
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

    private SprintTask findTask(Long id) {
        var taskOptional = sprintTaskRepo.findById(id);
        if (taskOptional.isEmpty()) return null;

        return taskOptional.get();
    }

}