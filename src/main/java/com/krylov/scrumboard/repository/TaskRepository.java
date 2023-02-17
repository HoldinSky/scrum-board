package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(nativeQuery = true,
            value = "SELECT * FROM task WHERE started_at IS NULL ORDER BY priority")
    List<Task> findAllBacklog();

    @Query(nativeQuery = true,
            value = "SELECT * FROM task WHERE started_at IS NOT NULL AND finished_at IS NULL ORDER BY priority")
    List<Task> findAllInProgress();

    @Query(nativeQuery = true,
            value = "SELECT * FROM task WHERE finished_at IS NOT NULL")
    List<Task> findAllFinished();
}
