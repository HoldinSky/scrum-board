package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.ActiveUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActiveUserRepository extends JpaRepository<ActiveUser, Long> {

    Optional<ActiveUser> findByUsername(String username);

}
