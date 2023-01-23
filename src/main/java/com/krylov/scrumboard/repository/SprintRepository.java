package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.Sprint;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
@Transactional
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    Optional<Sprint> findByStartOfSprint(Timestamp startOfSprint);

}
