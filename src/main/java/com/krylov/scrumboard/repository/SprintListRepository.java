package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.SprintList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SprintListRepository extends JpaRepository<SprintList, Long> {

    Optional<SprintList> findByState(String state);
}
