package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Role;

import java.util.Collection;

public interface UserService {
    AppUser saveUser(AppUser user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    AppUser getUser(String username);
    AppUser deleteUser(String username);
    Collection<AppUser> getUsers();
}
