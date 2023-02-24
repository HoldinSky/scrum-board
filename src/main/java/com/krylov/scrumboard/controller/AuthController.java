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
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final static ObjectMapper MAPPER = new ObjectMapper();
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AppUser> saveUser(@RequestBody RegistrationRequest request) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/auth/register").toUriString());
        AppUser user = authenticationService.register(request);

        if (user == null) return ResponseEntity.status(BAD_REQUEST.value())
                .header("Error_message", "The email is already taken!").build();
        return ResponseEntity.created(uri).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        log.info("Request for log in has been sent");
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @SneakyThrows
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam(name = "token") String refreshToken) {
        Map<String, String> tokens = authenticationService.refreshTokens(refreshToken);

        return ResponseEntity.ok(tokens);
    }

    @GetMapping
    public String testing() {
        return JSONObject.quote("This is testing request for CORS configuration!");
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam(name = "token") String token, @AuthenticationPrincipal AppUser user) {
        log.info("User is {}", user);
        return ResponseEntity.ok(authenticationService.validate(token, user));
    }

}

