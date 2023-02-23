package com.krylov.scrumboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.security.helper.AuthenticationRequest;
import com.krylov.scrumboard.security.helper.RegistrationRequest;
import com.krylov.scrumboard.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AppUser> saveUser(@RequestBody RegistrationRequest request) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/users").toUriString());
        return ResponseEntity.created(uri).body(authenticationService.register(request));
    }

    // TODO: IMPLEMENT AUTHENTICATION PROCEDURE (USE URLENCODED PARAMS AS CREDENTIALS)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok().body(authenticationService.authenticate(request));
    }

    @SneakyThrows
    @PostMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> tokens = authenticationService.refreshTokens(request);

        if (tokens == null) {
            response.setStatus(BAD_REQUEST.value());
            response.setContentType(APPLICATION_JSON_VALUE);

            Map<String, String> errors = new HashMap<>();
            errors.put("error_message", "Refresh token is missing");
            MAPPER.writeValue(response.getOutputStream(), errors);
        } else {
            response.setContentType(APPLICATION_JSON_VALUE);
            MAPPER.writeValue(response.getOutputStream(), tokens);
        }
    }
}

