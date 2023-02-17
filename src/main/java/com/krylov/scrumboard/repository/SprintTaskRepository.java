package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.SprintTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintTaskRepository extends JpaRepository<SprintTask, Long> {

    @Query(nativeQuery = true,
            value = "SELECT st.id, st.description, st.created_at, st.started_at, st.finished_at," +
                    " st.difficulty, st.priority, st.sprint_id, st.project_id FROM sprint_task st" +
                    " JOIN project p ON p.id = st.project_id WHERE st.finished_at IS NULL" +
                    " AND p.id = :projectId")
    List<SprintTask> retrieveBacklog(Long projectId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM sprint_task WHERE sprint_id = :sprintId")
    List<SprintTask> retrieveTasksOfSprintById(Long sprintId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM sprint_task WHERE started_at IS NULL")
    List<SprintTask> retrievePlannedSprintTask(Long id);

    @Query(nativeQuery = true,
            value = "SELECT * FROM sprint_task WHERE started_at IS NOT NULL AND finished_at IS NULL")
    List<SprintTask> retrieveInProgressSprintTask(Long id);

    @Query(nativeQuery = true,
            value = "SELECT * FROM sprint_task WHERE finished_at IS NOT NULL")
    List<SprintTask> retrieveFinishedSprintTask(Long id);

}
