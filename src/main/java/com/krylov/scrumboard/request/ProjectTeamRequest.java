package com.krylov.scrumboard.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectTeamRequest {
    private Long projectId;
    private String projectName;
    private Long teamId;
}