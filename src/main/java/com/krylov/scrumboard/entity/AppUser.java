package com.krylov.scrumboard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

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
            foreignKey = @ForeignKey(name = "app_user_id_fkey"),
            inverseForeignKey = @ForeignKey(name = "role_id_fkey"))
    private Collection<Role> roles = new ArrayList<>();

    @ManyToOne(cascade = {PERSIST, REFRESH, DETACH, MERGE}, fetch = EAGER)
    @JoinTable(name = "app_user_team",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"),
            foreignKey = @ForeignKey(name = "app_user_id_fkey"),
            inverseForeignKey = @ForeignKey(name = "team_id_fkey"))
    @JsonBackReference
    private Team team;

    public AppUser(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }
}
