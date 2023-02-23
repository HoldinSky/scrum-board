package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.repository.AppUserRepository;
import com.krylov.scrumboard.repository.RoleRepository;
import com.krylov.scrumboard.security.helper.RegistrationRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final AppUserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = getUser(username);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public AppUser saveUser(RegistrationRequest request) {
        AppUser user = new AppUser(
                request.getFirstname(),
                request.getLastname(),
                request.getEmail(),
                encoder.encode(request.getPassword())
        );
        user.getRoles().add(getRole("ROLE_USER"));
        log.info("Saving user {} to the database", user.getEmail());
        return userRepo.save(user);
    }

    @Override
    public AppUser deleteUser(String username) {
        log.info("Deleting user {} from the database", username);
        AppUser appUser = null;
        try {
            appUser = userRepo.findByEmail(username).orElseThrow(() -> new RuntimeException("Username is not found in database: " + username));
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }

        if (appUser != null) {
            userRepo.delete(appUser);
            return appUser;
        }
        return new AppUser();
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving role {} to the database", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public Role getRole(String name) {
        try {
            return roleRepo.findByName(name).orElseThrow(() ->
                    new RuntimeException("Role is not found in database with name: " + name));
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
        }
        return null;
    }

    @Override
    public AppUser addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to the user {}", roleName, username);
        try {
            AppUser appUser = userRepo.findByEmail(username).orElseThrow(() -> new RuntimeException("Username is not found in database: " + username));
            Role role = roleRepo.findByName(roleName).orElseThrow(() -> new RuntimeException("Role is not found in database: " + roleName));
            appUser.getRoles().add(role);
            return appUser;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    @Override
    public AppUser getUser(String username) {
        log.info("Retrieving user {} from the database", username);
        return userRepo.findByEmail(username).orElseThrow(() -> new RuntimeException("Username is not found in database: " + username));
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Retrieving all users from the database");
        return userRepo.findAll();
    }
}
