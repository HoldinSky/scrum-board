package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.security.helper.RegistrationRequest;

import java.util.List;

public interface UserService {
    AppUser saveUser(RegistrationRequest request) throws RuntimeException;
    Role saveRole(Role role);
    AppUser addRoleToUser(String username, String roleName) throws RuntimeException;
    AppUser getUser(String username) throws RuntimeException;
    Role getRole(String name) throws RuntimeException;
    AppUser deleteUser(String username) throws RuntimeException;
    List<AppUser> getUsers();
}
