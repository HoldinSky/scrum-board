package com.krylov.scrumboard.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.security.helper.AuthenticationRequest;
import com.krylov.scrumboard.security.helper.AuthenticationResponse;
import com.krylov.scrumboard.security.helper.RegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${secret.key.for.jwt}")
    private String SECRET_KEY;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder encoder;

    public AppUser register(RegistrationRequest request) {
        AppUser user = new AppUser(
                request.getFirstname(),
                request.getLastname(),
                request.getEmail(),
                encoder.encode(request.getPassword())
        );

        user.getRoles().add(new Role("ROLE_USER"));
        userService.saveUser(user);
        return user;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        log.info("Trying to authenticate. Username is {} and password is {}", username, password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
        Authentication authentication = authenticationManager.authenticate(authToken);

        User user = (User)authentication.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());

        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)))
                .withIssuer("/api/v1/auth/login")
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)))
                .withIssuer("/api/v1/auth/login")
                .sign(algorithm);

        AppUser appUser = userService.getUser(username);
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        return new AuthenticationResponse(appUser, tokens);
    }

    public Map<String, String> refreshTokens(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String refreshToken = authHeader.substring("Bearer ".length());
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decoded = verifier.verify(refreshToken);

            String username = decoded.getSubject();
            AppUser user = userService.getUser(username);

            String accessToken = JWT.create()
                    .withSubject(user.getEmail())
                    .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)))
                    .withIssuer(request.getRequestURI())
                    .withClaim("roles", user.getRoles().stream().map(Role::getName).toList())
                    .sign(algorithm);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            return tokens;
        }
        return null;
    }
}
