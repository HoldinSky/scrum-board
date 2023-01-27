package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.SprintTask;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface SprintTaskRepository extends JpaRepository<SprintTask, Long> {

    @Query(nativeQuery = true,
            value = "SELECT st.id, st.description, st.created_at, st.difficulty, st.priority FROM sprint_task st" +
                    " JOIN sprint s ON st.sprint_id = s.id JOIN project p ON p.id = s.project_id WHERE st.started_at IS NULL" +
                    " AND p.name = :projectName")
    List<SprintTask> retrieveBacklog(String projectName);

    @Query(nativeQuery = true,
            value = "SELECT * FROM sprint_task WHERE sprint_id = :sprintId")
    List<SprintTask> retrieveTasksOfSprintById(Long sprintId);

}
