package com.krylov.scrumboard.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberTeamRequest {
    private String username;
    private Long teamId;
}
