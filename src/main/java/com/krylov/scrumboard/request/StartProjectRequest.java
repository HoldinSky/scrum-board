package com.krylov.scrumboard.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StartProjectRequest {

    private Long projectId;
    private String sprintStart;
    private String sprintDuration;

}
