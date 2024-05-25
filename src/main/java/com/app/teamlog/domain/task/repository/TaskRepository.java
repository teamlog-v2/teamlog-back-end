package com.app.teamlog.domain.task.repository;

import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProject(Project project);
}
