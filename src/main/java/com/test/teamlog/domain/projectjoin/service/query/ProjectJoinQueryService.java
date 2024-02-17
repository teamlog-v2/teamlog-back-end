package com.test.teamlog.domain.projectjoin.service.query;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import com.test.teamlog.domain.projectjoin.repository.ProjectJoinRepository;
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
