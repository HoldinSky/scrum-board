package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.repository.AppUserRepository;
import com.krylov.scrumboard.repository.RoleRepository;
import com.krylov.scrumboard.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service @RequiredArgsConstructor @Transactional @Slf4j
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepo;
    private final RoleRepository roleRepo;

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving user {} to the database", user.getEmail());
        return userRepo.save(user);
    }

    @Override
    public AppUser deleteUser(String username) {
        log.info("Deleting user {} from the database", username);
        AppUser appUser = null;
        try {
            appUser = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username is not found in database: " + username));
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
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to the user {}", roleName, username);
        try {
            AppUser appUser = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username is not found in database: " + username));
            Role role = roleRepo.findByName(roleName).orElseThrow(() -> new RuntimeException("Role is not found in database: " + roleName));
            appUser.getRoles().add(role);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @Override
    public AppUser getUser(String username) {
        log.info("Retrieving user {} from the database", username);
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username is not found in database: " + username));
    }

    @Override
    public Collection<AppUser> getUsers() {
        log.info("Retrieving all users from the database");
        return userRepo.findAll();
    }
}
