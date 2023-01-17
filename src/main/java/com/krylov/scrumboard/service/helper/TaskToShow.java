package com.krylov.scrumboard.service.helper;

import lombok.*;
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class TaskToShow {

    public TaskToShow (Long id,
                       String description,
                       String createdAt) {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
    }

    private Long id;
    private String description;
    private String createdAt;
    private String startedAt;
    private String finishedAt;
    private Byte difficulty;

}
