package com.krylov.scrumboard.service.logic;

import com.krylov.scrumboard.entity.Task;
import com.krylov.scrumboard.repository.TaskRepository;
import com.krylov.scrumboard.service.LocalDateTimeConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TaskService {

    private TaskRepository taskRepository;
    private LocalDateTimeConverter converter;

    public void save(String request) {
        // convert publication time to database timestamp
        LocalDateTime dateTime = LocalDateTime.now();
        Timestamp createdAt = converter.convertToDatabaseColumn(dateTime);

        Task task = new Task(request, createdAt);
        taskRepository.save(task);
    }

}
