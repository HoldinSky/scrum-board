package com.krylov.scrumboard.service.helper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class SprintProperties {

    private LocalDate start;
    private LocalDate end;
    private Duration duration;
}
