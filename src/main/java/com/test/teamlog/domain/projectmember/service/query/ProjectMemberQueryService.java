package com.test.teamlog.domain.projectmember.service.query;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectmember.entity.ProjectMember;
import com.test.teamlog.domain.projectmember.repository.ProjectMemberRepository;
import com.test.teamlog.global.exception.ResourceForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectMemberQueryService {
    private final ProjectMemberRepository projectMemberRepository;

    public List<ProjectMember> findByProject(Project project) {
        return projectMemberRepository.findByProject(project);
    }

    public Optional<ProjectMember> findByProjectAndUser(Project project, User user) {
        return projectMemberRepository.findByProjectAndUser(project, user);
    }

    public boolean isProjectMember(Project project, User user) {
        return projectMemberRepository.findByProjectAndUser(project, user).isPresent();
    }


    // TODO: 완전 query의 성격은 아닌 것 같은데 위치 고민 필요
    public void validateProjectMember(Project project, User currentUser) {
        if (currentUser == null) {
            throw new ResourceForbiddenException("권한이 없습니다.\n로그인 해주세요.");
        }

        if (!isProjectMember(project, currentUser)) {
            throw new ResourceForbiddenException("권한이 없습니다.\n(프로젝트 멤버 아님)");
        }
    }
}
