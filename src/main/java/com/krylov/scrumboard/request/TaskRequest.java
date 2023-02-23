package com.krylov.scrumboard.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    private String description;
    private Byte difficulty;
    private Byte priority;

}
