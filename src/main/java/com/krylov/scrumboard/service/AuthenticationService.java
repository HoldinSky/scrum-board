package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.enums.Role;
import com.krylov.scrumboard.repository.AppUserRepository;
import com.krylov.scrumboard.request.AuthenticationRequest;
import com.krylov.scrumboard.request.RegistrationRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegistrationRequest request) {
        if (!request.getPassword().equals(request.getRepeatPassword()))
            return null;

        var user = AppUser.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        if (user.getEmail().equals("nkrylov2004@gmail.com")) user.setRole(Role.ADMIN);
        userRepo.save(user);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(user)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepo.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(user)
                .build();
    }
}
