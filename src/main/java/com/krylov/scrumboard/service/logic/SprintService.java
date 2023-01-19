package com.krylov.scrumboard.service.logic;

import com.krylov.scrumboard.repository.SprintRepository;
import com.krylov.scrumboard.service.bean.SprintConfiguration;
import com.krylov.scrumboard.service.helper.Duration;
import com.krylov.scrumboard.service.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.service.request.SprintRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SprintService {

    private SprintConfiguration sprintConfiguration;

    private LocalDateTimeConverter converter;

    private SprintRepository sprintRepository;

    public SprintService() {
        sprintConfiguration = new SprintConfiguration();
        converter = new LocalDateTimeConverter();

        sprintConfiguration.setConverter(converter);
    }

    public SprintConfiguration configureSprint(SprintRequest request) {
        sprintConfiguration.setStartOfSprint(request.getStartOfSprint());
        sprintConfiguration.setSprintDuration(Duration.valueOf(request.getSprintDuration()));

        return sprintConfiguration;
    }



}