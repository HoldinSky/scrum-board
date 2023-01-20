package com.krylov.scrumboard.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity(name = "AppUser")
@Table(name = "AppUser")
public class AppUser {

    @Id
    @SequenceGenerator(name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "user_sequence")
    private Long id;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            mappedBy = "user")
    private List<Task> taskList;



    public void addTask(Task task) {
        if (taskList == null) taskList = new ArrayList<>();
        taskList.add(task);
    }

}
