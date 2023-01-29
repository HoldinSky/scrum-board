package com.krylov.scrumboard.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.krylov.scrumboard.helper.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode

@Table(name = "Project")
@Entity(name = "Project")
public class Project {

    @Id
    @SequenceGenerator(name = "project_sequence",
            sequenceName = "project_sequence",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "project_sequence")
    private Long id;

    @Column(name = "name",
            unique = true,
            nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            nullable = false)
    private Status status;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            mappedBy = "project")
    @JsonManagedReference
    private List<Sprint> sprintList;

    public void addSprint(Sprint sprint) {
        if (sprintList == null) sprintList = new ArrayList<>();
        sprintList.add(sprint);
    }

    public Project(String name) {
        this.name = name;
        this.status = Status.PLANNED;
    }
}
