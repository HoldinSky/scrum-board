package com.krylov.scrumboard.security.helper;

import com.krylov.scrumboard.entity.Role;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String repeatPassword;

}

