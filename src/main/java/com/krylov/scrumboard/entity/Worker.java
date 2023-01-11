package com.krylov.scrumboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Worker")
@Table(name = "Worker",
        uniqueConstraints = {
                @UniqueConstraint(name = "email_unique",
                        columnNames = "email")
        })

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Worker {

    @Id
    @SequenceGenerator(name = "worker_sequence",
            sequenceName = "worker_sequence",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "worker_sequence")
    @Column(name = "id",
            updatable = false)
    private Long id;

    @Column(name = "first_name",
            nullable = false,
            length = 100)
    private String firstName;

    @Column(name = "last_name",
            nullable = false,
            length = 100)
    private String lastName;

    @Column(name = "email",
            nullable = false,
            length = 150)
    private String email;

    @ManyToOne
    @JoinColumn(name = "task_id",
            foreignKey = @ForeignKey(name = "task_id_fkey"))
    private Task task;

    public Worker(String firstName,
                  String lastName,
                  String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
