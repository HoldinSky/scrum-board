package com.krylov.scrumboard.security.helper;

import com.krylov.scrumboard.entity.AppUser;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private AppUser user;

}
