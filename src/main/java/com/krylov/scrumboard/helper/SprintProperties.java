package com.krylov.scrumboard.helper;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SprintProperties {

    private LocalDate start;
    private LocalDate end;
    private Duration duration;
}
