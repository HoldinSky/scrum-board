package com.krylov.scrumboard.service.logic;

import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.entity.Task;
import com.krylov.scrumboard.entity.Worker;
import com.krylov.scrumboard.repository.WorkerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WorkerService {

    private WorkerRepository workerRepository;

    public List<Worker> retrieveAll() {
        return workerRepository.findAll();
    }

    public Worker retrieveById(Long id) {
        Optional<Worker> optional = workerRepository.findById(id);
        if (optional.isEmpty()) return null;
        return optional.get();
    }

    public SprintTask retrieveTaskById(Long id) {
        Optional<Worker> optional = workerRepository.findById(id);
        if (optional.isEmpty()) return null;
        return optional.get().getTask();
    }

}
