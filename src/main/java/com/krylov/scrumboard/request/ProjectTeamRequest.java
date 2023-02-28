package com.krylov.scrumboard.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTeamRequest {
    private Long projectId;
    private String projectName;
    private Long teamId;
}
