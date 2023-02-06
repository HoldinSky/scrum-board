package com.krylov.scrumboard.bean;

import com.krylov.scrumboard.entity.ActiveUser;
import com.krylov.scrumboard.repository.ActiveUserRepository;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@Data

@NoArgsConstructor
@AllArgsConstructor
public class LoggedUser implements HttpSessionBindingListener {

    private Long id;
    private String username;
    private ActiveUserRepository repository;

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        LoggedUser user = (LoggedUser) event.getValue();
        if (!repository.existsById(user.id)) {
            repository.save(new ActiveUser(user.getId(), user.getUsername()));
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        LoggedUser user = (LoggedUser) event.getValue();
        if (repository.existsById(user.id)) {
            repository.deleteById(user.id);
        }
    }
}
