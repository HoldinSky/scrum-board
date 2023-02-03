package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.AppUser;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String token;
    private AppUser user;

}
