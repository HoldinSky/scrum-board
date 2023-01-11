package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.Worker;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface WorkerRepository extends JpaRepository<Worker, Long> {

}
