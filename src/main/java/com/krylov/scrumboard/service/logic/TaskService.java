package com.krylov.scrumboard.service.logic;

import com.krylov.scrumboard.entity.Task;
import com.krylov.scrumboard.entity.Worker;
import com.krylov.scrumboard.repository.TaskRepository;
import com.krylov.scrumboard.service.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.service.helper.MyDateTimeFormatter;
import com.krylov.scrumboard.service.helper.TaskToShow;
import com.krylov.scrumboard.service.request.TaskRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {

    private TaskRepository taskRepository;
    private LocalDateTimeConverter converter;
    private MyDateTimeFormatter formatter;
    private List<TaskToShow> taskList;

    public void save(TaskRequest request) {
        // convert publication time to database timestamp
        LocalDateTime dateTime = LocalDateTime.now();
        Timestamp createdAt = converter.convertToDatabaseColumn(dateTime);


        Task task = new Task(request.getDescription(), createdAt);
        if (request.getDifficulty() != null) task.setDifficulty(request.getDifficulty());
        taskRepository.save(task);
    }

    public List<TaskToShow> retrieveALl() {
        taskList = new ArrayList<>();

        for (Task t : taskRepository.findAll()) {
            Timestamp started = t.getStartedAt();
            Timestamp finished = t.getFinishedAt();
            Byte dif = t.getDifficulty();

            TaskToShow tts = new TaskToShow(
                    t.getId(),
                    t.getDescription(),
                    formatter.format(converter.convertToEntityAttribute(t.getCreatedAt())));

            if (started != null) tts.setStartedAt(formatter.format(converter.convertToEntityAttribute(started)));
            if (finished != null) tts.setFinishedAt(formatter.format(converter.convertToEntityAttribute(finished)));
            if (t.getDifficulty() != null) tts.setDifficulty(t.getDifficulty());

            taskList.add(tts);
        }

        return taskList;
    }

    public TaskToShow retrieveById(Long id) {
        Optional<Task> optional = taskRepository.findById(id);
        if (optional.isEmpty()) return null;

        Task t = optional.get();

        TaskToShow tts = new TaskToShow(
                t.getId(),
                t.getDescription(),
                formatter.format(converter.convertToEntityAttribute(t.getCreatedAt())));

        if (t.getStartedAt() != null) tts.setStartedAt(formatter.format(converter.convertToEntityAttribute(t.getStartedAt())));
        if (t.getFinishedAt() != null) tts.setFinishedAt(formatter.format(converter.convertToEntityAttribute(t.getFinishedAt())));
        if (t.getDifficulty() != null) tts.setDifficulty(t.getDifficulty());

        return tts;
    }

    public List<Worker> retrieveWorkersById(Long id) {
        Optional<Task> optional = taskRepository.findById(id);
        if (optional.isEmpty()) return null;
        return optional.get().getWorkerList();
    }

    public Task updateTask(Long id, Byte difficulty, String request) {
        // retrieve task from DB
        Optional<Task> optional = taskRepository.findById(id);
        // if there is not such task -> return
        if(optional.isEmpty()) return new Task("Could not find such a task");

        Task task = optional.get();
        LocalDateTime dateTime = LocalDateTime.now();

        // based on request complete actions
        switch (request) {
            case "start" -> {
                if (difficulty == null && task.getDifficulty() == null) return new Task("Task must have its difficulty");
                if (task.getDifficulty() == null) task.setDifficulty(difficulty);
                task.setStartedAt(converter.convertToDatabaseColumn(dateTime));
            }
            case "finish" -> {
                if (task.getStartedAt() == null) return new Task("This task was not started yet");
                task.setFinishedAt(converter.convertToDatabaseColumn(dateTime));
            }
            case "setDifficulty" -> {
                if (difficulty == 0) return new Task("Task must have its difficulty");
                task.setDifficulty(difficulty);
            }
            default -> {
                System.out.println("DEBUG: Cannot recognize update request \"" + request + "\"");
            }
        }

        // update DB instance
        taskRepository.save(task);
        return task;
    }

    public void deleteTask(Long id) {
        // retrieve task from DB
        Optional<Task> optional = taskRepository.findById(id);
        // if there is not such task -> return
        if(optional.isEmpty()) return;

        Task task = optional.get();
        taskRepository.delete(task);
    }
}
