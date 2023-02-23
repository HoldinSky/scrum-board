package com.krylov.scrumboard.security.helper;

import lombok.*;

@Data
@AllArgsConstructor
public class AuthenticationRequest {

    private String username;
    private String password;

}
