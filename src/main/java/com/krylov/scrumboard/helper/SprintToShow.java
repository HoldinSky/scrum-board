package com.krylov.scrumboard.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SprintToShow {

    private Long id;
    private String start;
    private String finish;

}