package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.Sprint;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    Optional<Sprint> findByStartOfSprint(Timestamp startOfSprint);

    @Query(nativeQuery = true,
            value = "SELECT s.id, s.duration, s.start, s.finish, s.project_id FROM sprint AS s" +
                    " JOIN project AS p ON p.id = s.project_id WHERE p.status = 'IN_PROGRESS'")
    List<Sprint> findAllActiveSprints();
}
