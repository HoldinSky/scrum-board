package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.security.helper.RegistrationRequest;

import java.util.List;

public interface UserService {
    AppUser saveUser(RegistrationRequest request);
    Role saveRole(Role role);
    AppUser addRoleToUser(String username, String roleName);
    AppUser getUser(String username);
    AppUser deleteUser(String username);
    List<AppUser> getUsers();
}
