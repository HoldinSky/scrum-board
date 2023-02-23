package com.krylov.scrumboard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.krylov.scrumboard.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor

@Table(name = "Project",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "project_name_unique",
                        columnNames = "name"
                )
        }
)
@Entity(name = "Project")
public class Project {

    @Id @GeneratedValue(strategy = AUTO)
    private Long id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            nullable = false)
    private Status status;

    @OneToMany(fetch = LAZY,
            cascade = ALL,
            mappedBy = "project")
    @JsonBackReference
    private List<Sprint> sprintList = new ArrayList<>();

    public Project(String name) {
        this.name = name;
        this.status = Status.PLANNED;
    }

}
