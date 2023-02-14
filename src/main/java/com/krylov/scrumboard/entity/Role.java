package com.krylov.scrumboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Entity @Table
public class Role {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

}
