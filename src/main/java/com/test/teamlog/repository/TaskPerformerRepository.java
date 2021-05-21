package com.test.teamlog.repository;

import com.test.teamlog.entity.Task;
import com.test.teamlog.entity.TaskPerformer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskPerformerRepository extends JpaRepository<TaskPerformer, Long> {
    List<TaskPerformer> findAllByTask(Task task);
}