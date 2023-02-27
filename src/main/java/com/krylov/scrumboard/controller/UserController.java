package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.security.helper.RegistrationRequest;
import com.krylov.scrumboard.service.UserService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<AppUser>> getUsers() {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user").toUriString());
        return ResponseEntity.created(uri).body(userService.getUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<AppUser> getUser(@PathVariable(name = "username")String username) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/" + username).toUriString());
        return ResponseEntity.created(uri).body(userService.getUser(username));
    }

    @PostMapping("/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/role").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PutMapping("/roleToUser")
    public ResponseEntity<AppUser> addRoleToUser(@RequestBody RoleToUser request) {
        return ResponseEntity.ok(userService.addRoleToUser(request.getUsername(), request.getRole()));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<AppUser> deleteUser(@PathVariable(name = "username") String username) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/" + username).toUriString());
        return ResponseEntity.created(uri).body(userService.deleteUser(username));
    }

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RoleToUser {
    private String username;
    private String role;
}
