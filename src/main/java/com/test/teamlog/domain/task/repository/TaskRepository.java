package com.test.teamlog.domain.task.repository;

import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProject(Project project);
}
