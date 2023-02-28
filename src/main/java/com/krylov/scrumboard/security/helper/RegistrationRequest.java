package com.krylov.scrumboard.security.helper;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    private String firstname;
    private String lastname;
    private String username;
    private String password;

}

