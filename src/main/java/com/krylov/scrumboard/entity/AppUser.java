package com.krylov.scrumboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.CascadeType.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "AppUser")
@Table(name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "user_email_unique",
                        columnNames = "email")
        }
)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "first_name",
            length = 100)
    private String firstname;

    @Column(name = "last_name",
            length = 100)
    private String lastname;

    @Column(name = "email",
            updatable = false,
            nullable = false)
    private String email;

    @Column(name = "password",
            nullable = false)
    private String password;

    @ManyToMany(fetch = EAGER,
            cascade = {PERSIST, REFRESH, DETACH, MERGE})
    @JoinTable(name = "app_user_roles",
            foreignKey = @ForeignKey(name ="app_user_id_fkey"),
            inverseForeignKey = @ForeignKey(name = "role_id_fkey"))
    private Collection<Role> roles = new ArrayList<>();

    public AppUser(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }
}
