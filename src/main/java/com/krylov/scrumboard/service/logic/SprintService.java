package com.krylov.scrumboard.service.logic;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.entity.SprintTask;
import com.krylov.scrumboard.repository.SprintTaskRepository;
import com.krylov.scrumboard.service.bean.SprintConfigurer;
import com.krylov.scrumboard.service.helper.Duration;
import com.krylov.scrumboard.service.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.service.request.SprintRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SprintService {

    private SprintConfigurer sprintConfigurer;

    private LocalDateTimeConverter converter;

    private SprintTaskRepository sprintTaskRepository;

    public SprintService() {
        sprintConfigurer = new SprintConfigurer();
        converter = new LocalDateTimeConverter();

        sprintConfigurer.setConverter(converter);
    }

    public SprintConfigurer configureSprint(SprintRequest request) {
        sprintConfigurer.setStartOfSprint(request.getStartOfSprint());
        sprintConfigurer.setSprintDuration(Duration.valueOf(request.getSprintDuration()));

        Thread thread = new Thread(sprintConfigurer);
        thread.start();

        return sprintConfigurer;
    }

    public void addSprintTaskById(Long id) {
        Optional<SprintTask> optional = sprintTaskRepository.findById(id);
        if (optional.isEmpty()) return;

        var task = optional.get();
        sprintConfigurer.addTask(task);
    }

    public void addMultipleTasks(List<Long> idList) {
        var list = sprintTaskRepository.findAllById(idList);

        sprintConfigurer.addMultipleTasks(list);
    }

    public Sprint getSprintById(Long id) {
        return sprintConfigurer.getSprintDraft();
    }

    public List<SprintTask> retrieveBacklog() {
        return sprintTaskRepository.findAll();
    }


}