package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.Task;
import com.krylov.scrumboard.helper.TaskOrError;
import com.krylov.scrumboard.repository.TaskRepository;
import com.krylov.scrumboard.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.request.TaskRequest;
import com.krylov.scrumboard.request.UpdateTaskRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class TaskService {

    private TaskRepository taskRepository;
    private LocalDateTimeConverter converter;

    public Task save(TaskRequest request) {
        // convert publication time to database timestamp
        LocalDateTime dateTime = LocalDateTime.now();
        Timestamp createdAt = converter.convertToDatabaseColumn(dateTime);


        Task task = new Task(request.getDescription(), createdAt, request.getPriority());
        if (request.getDifficulty() != null) task.setDifficulty(request.getDifficulty());
        taskRepository.save(task);
        return task;
    }

    public Task getTaskById(Long id) {
        Task task = null;
        try {
            task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task is not found in database with id: " + id));
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
        }
        return task;
    }

    public List<Task> getAllBacklog() {
        return taskRepository.findAllBacklog();
    }

    public List<Task> getAllInProgress() {
        return taskRepository.findAllInProgress();
    }

    public List<Task> getAllFinished() {
        return taskRepository.findAllFinished();
    }

    public TaskOrError updateTask(Long id, UpdateTaskRequest request) {

        try {
            // retrieve task from DB
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Task is not found with the id: " + id));

            String action = request.getAction();
            Byte difficulty = request.getDifficulty();
            LocalDateTime dateTime = LocalDateTime.now();

            // based on request complete actions
            switch (action) {
                case "start" -> {
                    if (difficulty == null && task.getDifficulty() == null) {
                        log.error("Task must have its difficulty to start");
                        return new TaskOrError(null, "Task must have its difficulty to start");
                    }
                    if (task.getDifficulty() == null) task.setDifficulty(difficulty);
                    task.setStartedAt(converter.convertToDatabaseColumn(dateTime));
                }
                case "finish" -> {
                    if (task.getStartedAt() == null) {
                        log.error("Task was not started yet");
                        return new TaskOrError(null, "Task was not started yet");
                    }
                    task.setFinishedAt(converter.convertToDatabaseColumn(dateTime));
                }
                case "Set difficulty" -> {
                    if (difficulty == 0) {
                        log.error("Task must have its difficulty");
                        return new TaskOrError(null, "Task must have its difficulty");
                    }
                    task.setDifficulty(difficulty);
                }
                default -> {
                    log.error("Cannot recognize update request \"" + request + "\"");
                    return new TaskOrError(null, "Cannot recognize update request \"" + request + "\"");
                }
            }
//            // update DB instance
//            taskRepository.save(task);
            return new TaskOrError(task, null);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    public Task deleteTask(Long id) {
        try {
            // retrieve task from DB
            Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task is not found with the id: " + id));
            taskRepository.delete(task);
            return task;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
        }
        return null;
    }
}
