package com.krylov.scrumboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor

@Entity(name = "SprintList")
@Table(name = "SprintList")
public class SprintList {

    @Id
    @Column(name = "sprint_id",
            unique = true,
            nullable = false)
    private Long sprintId;

    private String state;   // current/next
}
