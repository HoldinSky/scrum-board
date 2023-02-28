package com.krylov.scrumboard.security.helper;

import com.krylov.scrumboard.entity.AppUser;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private AppUser user;
    private Map<String, String> tokens;
}
