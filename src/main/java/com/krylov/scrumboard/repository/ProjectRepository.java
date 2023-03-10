package com.krylov.scrumboard.repository;

import com.krylov.scrumboard.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByName(String name);

    @Query(nativeQuery = true,
            value = "SELECT * FROM project WHERE status = 'IN_PROGRESS'")
    List<Project> findAllActiveProjects();
}
