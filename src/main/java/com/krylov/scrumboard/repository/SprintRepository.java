package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    Optional<Sprint> findByStartOfSprint(Timestamp startOfSprint);

    @Query(nativeQuery = true,
            value = "SELECT s.id AS id, s.duration AS duration, s.start AS start," +
                    " s.finish AS finish, s.project_id AS projectId," +
                    " p.name AS name, p.status AS status FROM sprint s" +
                    " JOIN project AS p ON p.id = s.project_id WHERE p.status = 'IN_PROGRESS'")
    List<SprintDTO> findAllActiveSprints();


    interface SprintDTO {
        Long getId();
        String getDuration();
        Timestamp getStart();
        Timestamp getFinish();
        Long getProjectId();
        String getName();
        String getStatus();

    }
}
