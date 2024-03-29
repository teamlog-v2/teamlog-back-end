package com.test.teamlog.domain.projectmember.service;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.project.service.query.ProjectQueryService;
import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import com.test.teamlog.domain.projectjoin.service.query.ProjectJoinQueryService;
import com.test.teamlog.domain.projectmember.dto.ProjectMemberReadResult;
import com.test.teamlog.domain.projectmember.entity.ProjectMember;
import com.test.teamlog.domain.projectmember.repository.ProjectMemberRepository;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.exception.BadRequestException;
import com.test.teamlog.global.exception.ResourceForbiddenException;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectQueryService projectQueryService;
    private final AccountQueryService accountQueryService;
    private final ProjectJoinQueryService projectJoinQueryService;

    // 프로젝트 멤버 추가
    @Transactional
    public ApiResponse create(Long projectId, Account currentAccount) {
        final Project project = findProjectById(projectId);
        ProjectJoin projectJoin = projectJoinQueryService.findByProjectAndAccount(project, currentAccount)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectInvitation", "ID", currentAccount.getIdentification()));

        // TODO: project_join과 함께 다시 생각해보기
        if (!projectJoin.getIsInvited() || projectJoin.getIsAccepted()) throw new BadRequestException("잘못된 요청입니다.");


        projectMemberRepository.save(ProjectMember.create(projectJoin.getProject(), projectJoin.getAccount()));
        return new ApiResponse(Boolean.TRUE, "프로젝트 멤버 가입 됨");
    }

    // 프로젝트 나가기
    @Transactional
    public ApiResponse leaveProject(Long projectId, Account currentAccount) {
        final Project project = findProjectById(projectId);

        if (project.getMaster().getIdentification().equals(currentAccount.getIdentification())) {
            throw new ResourceForbiddenException("마스터는 탈퇴할 수 없습니다.\n위임하고 다시 시도하세요.");
        }

        ProjectMember projectMember = projectMemberRepository.findByProjectAndAccount(project, currentAccount)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMemeber", "accountId", currentAccount.getIdentification()));
        projectMemberRepository.delete(projectMember);

        return new ApiResponse(Boolean.TRUE, "프로젝트 탈퇴 완료");
    }

    // 마스터 - 프로젝트 멤버 삭제
    @Transactional
    public ApiResponse expelMember(Long projectId, String accountId, Account currentAccount) {
        final Project project = findProjectById(projectId);
        Account account = accountQueryService.findByIdentification(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACCOUNT", "id", accountId));

        if (!project.isProjectMaster(account)) {
            throw new ResourceForbiddenException("프로젝트 마스터가 아닙니다.");
        }

        ProjectMember projectMember = projectMemberRepository.findByProjectAndAccount(project, account)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMemeber", "accountId", accountId));

        projectMemberRepository.delete(projectMember);
        return new ApiResponse(Boolean.TRUE, "멤버 삭제 완료");
    }


    // 프로젝트 멤버 조회
    public List<ProjectMemberReadResult> readAll(Long projectId) {
        final Project project = findProjectById(projectId);
        List<ProjectMember> projectMemberList = projectMemberRepository.findByProject(project);

        return projectMemberList.stream().map(ProjectMemberReadResult::of).toList();
    }

    private Project findProjectById(Long projectId) {
        return projectQueryService.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));
    }
}
