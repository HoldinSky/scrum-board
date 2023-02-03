package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.request.AuthenticationRequest;
import com.krylov.scrumboard.request.RegistrationRequest;
import com.krylov.scrumboard.service.AuthenticationResponse;
import com.krylov.scrumboard.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerNewUser(
            @ModelAttribute RegistrationRequest request,
            ModelAndView modelAndView) {

        AuthenticationResponse response = authenticationService.register(request);
        if (response == null) {
            modelAndView.addObject("passwordsDontMatch", true);
            return ResponseEntity.ok(new AuthenticationResponse("Passwords do not match", null));
        }

        modelAndView.setViewName("authenticate-form");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(
            @ModelAttribute AuthenticationRequest request,
            ModelAndView modelAndView) {

        modelAndView.setViewName("redirect:/api/v1");

        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/logout")
    public AppUser logout(String token) {
        return null;
    }

}
