package com.krylov.scrumboard.bean;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.repository.ActiveUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("myAuthenticationSuccessHandler")
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ActiveUserRepository repository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            LoggedUser user = new LoggedUser(
                    ((AppUser)authentication.getDetails()).getId(),
                    authentication.getName(),
                    repository);
            session.setAttribute("user", user);
        }
    }
}