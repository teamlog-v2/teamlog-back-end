package com.app.teamlog.domain.projectjoin.service.query;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.projectjoin.entity.ProjectJoin;
import com.app.teamlog.domain.projectjoin.repository.ProjectJoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectJoinQueryService {
    private final ProjectJoinRepository projectJoinRepository;

    public Optional<ProjectJoin> findByProjectAndAccount(Project project, Account account) {
        return projectJoinRepository.findByProjectAndAccount(project, account);
    }
}
