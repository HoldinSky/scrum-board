package com.krylov.scrumboard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor

@Entity(name = "Team")
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = ALL, fetch = EAGER)
    @JoinTable(name = "team_project",
            foreignKey = @ForeignKey(name = "team_id_fkey"),
            inverseForeignKey = @ForeignKey(name = "project_id_fkey"),
            indexes = @Index(name = "projects_id", columnList = "projects_id", unique = true),
            inverseJoinColumns = @JoinColumn(name = "projects_id")
    )
    private List<Project> projectList = new ArrayList<>();

    @OneToMany(cascade = {MERGE, PERSIST, DETACH, REFRESH}, fetch = LAZY)
    @JoinTable(name = "team_members",
            foreignKey = @ForeignKey(name = "team_id_fkey"),
            inverseForeignKey = @ForeignKey(name = "member_id_fkey"),
            indexes = @Index(name = "members_id", columnList = "members_id", unique = true),
            inverseJoinColumns = @JoinColumn(name = "members_id")
    )
    @JsonManagedReference
    private List<AppUser> members = new ArrayList<>();

    @OneToOne(fetch = EAGER, cascade = {MERGE, PERSIST, DETACH, REFRESH})
    @JoinColumn(name = "creator",
            foreignKey = @ForeignKey(name = "creator_id_fkey"),
            referencedColumnName = "id")
    private AppUser creator;

    public Team(String name) {
        this.name = name;
    }
}
