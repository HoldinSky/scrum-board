package com.krylov.scrumboard.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "AcriveUser")

@Data
@NoArgsConstructor
public class ActiveUser {

    @Id
    private Long user_id;

    @Column(name = "username",
            updatable = false,
            nullable = false,
            unique = true)
    private String username;

    public ActiveUser(Long id, String username) {
        this.user_id = id;
        this.username = username;
    }

}
