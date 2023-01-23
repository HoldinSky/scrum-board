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
            value = "SELECT * FROM sprint_task WHERE started_at IS NOT NULL")
    List<SprintTask> retrieveBacklog();

    @Query(nativeQuery = true,
            value = "SELECT * FROM sprint_task WHERE sprint_id = :sprintId")
    List<SprintTask> retrieveTasksOfSprintById(Long sprintId);

}
