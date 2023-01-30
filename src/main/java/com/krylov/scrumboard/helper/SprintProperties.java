package com.krylov.scrumboard.helper;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class SprintProperties {

    private LocalDate start;
    private LocalDate end;
    private Duration duration;
}
