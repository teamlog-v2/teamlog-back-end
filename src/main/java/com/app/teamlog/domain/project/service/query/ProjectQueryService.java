package com.app.teamlog.domain.project.service.query;

import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectQueryService {
    private final ProjectRepository projectRepository;

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }
}
