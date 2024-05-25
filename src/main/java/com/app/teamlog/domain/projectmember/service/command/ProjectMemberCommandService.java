package com.app.teamlog.domain.projectmember.service.command;

import com.app.teamlog.domain.projectmember.entity.ProjectMember;
import com.app.teamlog.domain.projectmember.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectMemberCommandService {
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public ProjectMember save(ProjectMember projectMember) {
        return projectMemberRepository.save(projectMember);
    }

    @Transactional
    public void delete(ProjectMember projectMember) {
        projectMemberRepository.delete(projectMember);
    }
}
