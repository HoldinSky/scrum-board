package com.krylov.scrumboard.service.helper;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class TaskToShow {

    private Long id;
    private String description;
    private String createdAt;
    private String startedAt;
    private String finishedAt;
    private Byte difficulty;

}
