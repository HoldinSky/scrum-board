package com.krylov.scrumboard.request;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartProjectRequest {

    private Long projectId;
    private String sprintStart;
    private String sprintDuration;

}
