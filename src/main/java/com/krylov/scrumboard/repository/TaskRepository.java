package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
@Transactional
public interface TaskRepository extends JpaRepository<Task, Long> {

}
