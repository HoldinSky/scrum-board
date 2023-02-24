package com.krylov.scrumboard.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.repository.AppUserRepository;
import com.krylov.scrumboard.security.JWTService;
import com.krylov.scrumboard.security.helper.AuthenticationRequest;
import com.krylov.scrumboard.security.helper.AuthenticationResponse;
import com.krylov.scrumboard.security.helper.RegistrationRequest;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.krylov.scrumboard.helper.RoleNames.USER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${secret.key.for.jwt}")
    private String SECRET_KEY;
    private final PasswordEncoder encoder;
    private final UserService userService;
    private final AppUserRepository repository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public AppUser register(RegistrationRequest request) {
        if (repository.findByEmail(request.getUsername()).isPresent()) {
            return null;
        }
        AppUser user = new AppUser(
                request.getFirstname(),
                request.getLastname(),
                request.getUsername(),
                encoder.encode(request.getPassword())
        );

        Role role = userService.getRole(USER);
        user.getRoles().add(role);
        userService.saveUser(user);
        return user;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        log.info("Trying to authenticate. Username is {} and password is {}", username, password);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username, password
        ));

        HashMap<String, Object> map = new HashMap<>();
        AppUser appUser = userService.getUser(username);
        map.put("authorities", appUser.getAuthorities());

        String accessToken = jwtService.generateToken(map, appUser);
        String refreshToken = jwtService.generateRefreshToken(map, appUser);

        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        return new AuthenticationResponse(appUser, tokens);
    }

    public Map<String, String> refreshTokens(String refreshToken) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decoded = verifier.verify(refreshToken);

        String username = decoded.getSubject();
        AppUser user = userService.getUser(username);

        String accessToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)))
                .withIssuer("/api/auth/refresh")
                .withClaim("roles", user.getRoles().stream().map(Role::getName).toList())
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;
    }

    public Boolean validate(String token, AppUser user) {
        try {
            return jwtService.isTokenValid(token, user);
        } catch (ExpiredJwtException e) {
            return false;
        }
    }
}
