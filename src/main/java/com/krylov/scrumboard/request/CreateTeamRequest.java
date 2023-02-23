package com.krylov.scrumboard.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public
class CreateTeamRequest {
    private String teamName;
    private String username;
}