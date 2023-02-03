package com.krylov.scrumboard.request;

import lombok.*;

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

