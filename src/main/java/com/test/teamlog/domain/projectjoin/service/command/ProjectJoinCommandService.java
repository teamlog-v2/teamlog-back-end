package com.test.teamlog.domain.projectjoin.service.command;

import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import com.test.teamlog.domain.projectjoin.repository.ProjectJoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectJoinCommandService {
    private final ProjectJoinRepository projectJoinRepository;

    public ProjectJoin save(ProjectJoin projectJoin) {
        return projectJoinRepository.save(projectJoin);
    }

    public void saveAll(Iterable<ProjectJoin> projectJoins) {
        projectJoinRepository.saveAll(projectJoins);
    }

    public void delete(ProjectJoin projectJoin) {
        projectJoinRepository.delete(projectJoin);
    }
}
