package com.krylov.scrumboard.security.helper;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    private String username;
    private String password;

}
