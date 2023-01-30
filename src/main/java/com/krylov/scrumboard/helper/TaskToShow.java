package com.krylov.scrumboard.helper;

import com.krylov.scrumboard.entity.Project;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class TaskToShow {

    public TaskToShow (Long id,
                       String description,
                       String createdAt,
                       Byte priority) {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
        this.priority = priority;
    }

    private Long id;
    private String description;
    private String createdAt;
    private String startedAt;
    private String finishedAt;
    private Byte difficulty;
    private Byte priority;

    private Project project;

}
