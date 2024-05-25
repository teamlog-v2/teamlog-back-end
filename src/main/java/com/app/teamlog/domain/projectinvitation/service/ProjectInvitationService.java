package com.app.teamlog.domain.projectinvitation.service;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.account.service.query.AccountQueryService;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.project.service.query.ProjectQueryService;
import com.app.teamlog.domain.projectinvitation.dto.*;
import com.app.teamlog.domain.projectinvitation.entity.ProjectInvitation;
import com.app.teamlog.domain.projectinvitation.repository.ProjectInvitationRepository;
import com.app.teamlog.domain.projectmember.entity.ProjectMember;
import com.app.teamlog.domain.projectmember.service.command.ProjectMemberCommandService;
import com.app.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.app.teamlog.global.dto.ApiResponse;
import com.app.teamlog.global.exception.BadRequestException;
import com.app.teamlog.global.exception.ResourceAlreadyExistsException;
import com.app.teamlog.global.exception.ResourceForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectInvitationService {
    private final ProjectInvitationRepository projectInvitationRepository;

    private final AccountQueryService accountQueryService;
    private final ProjectQueryService projectQueryService;
    private final ProjectMemberQueryService projectMemberQueryService;
    private final ProjectMemberCommandService projectMemberCommandService;

    @Transactional
    public ApiResponse create(ProjectInvitationCreateInput input) {
        final Long projectIdx = input.getProjectIdx();
        final Long inviterIdx = input.getInviterIdx();
        final Long inviteeIdx = input.getInviteeIdx();

        final Project project
                = prepareProject(projectIdx);
        final Account inviter
                = prepareAccount(inviterIdx);
        final Account invitee
                = prepareAccount(inviteeIdx);

        if (!projectMemberQueryService.isProjectMember(project, inviter)) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 프로젝트 멤버가 아님 )");
        }

        final ProjectInvitation projectInvitation
                = projectInvitationRepository.findByProjectAndInvitee(project, invitee).orElse(null);

        if (projectInvitation != null) {
            throw new ResourceAlreadyExistsException("이미 존재하는 프로젝트 초대입니다.");
        }

        projectInvitationRepository.save(input.toProjectInvitation(project, inviter, invitee));
        return new ApiResponse(Boolean.TRUE, "프로젝트 초대 성공");
    }

    @Transactional
    public ApiResponse accept(ProjectInvitationAcceptInput input) {
        final Long projectIdx = input.getProjectIdx();
        final Long inviteeIdx = input.getInviteeIdx();

        final Project project
                = prepareProject(projectIdx);
        final Account invitee
                = prepareAccount(inviteeIdx);

        final ProjectInvitation projectInvitation = prepareProjectInvitation(project, invitee);

        checkProjectMember(project, invitee);

        projectMemberCommandService.save(ProjectMember.create(project, invitee));
        projectInvitationRepository.delete(projectInvitation);

        return new ApiResponse(Boolean.TRUE, "프로젝트 초대 수락 성공");
    }

    @Transactional
    public ApiResponse reject(ProjectInvitationRejectInput input) {
        final Long projectIdx = input.getProjectIdx();
        final Long inviteeIdx = input.getInviteeIdx();

        final Project project
                = prepareProject(projectIdx);
        final Account invitee
                = prepareAccount(inviteeIdx);

        final ProjectInvitation projectInvitation = prepareProjectInvitation(project, invitee);

        projectInvitationRepository.delete(projectInvitation);

        return new ApiResponse(Boolean.TRUE, "프로젝트 초대 거절 성공");
    }

    @Transactional
    public ApiResponse cancel(ProjectInvitationCancelInput input) {
        final Project project = prepareProject(input.getProjectIdx());
        final Account inviter = prepareAccount(input.getInviterIdx());
        final Account invitee = prepareAccount(input.getInviteeIdx());

        checkProjectMember(project, inviter);

        final ProjectInvitation projectInvitation = prepareProjectInvitation(project ,invitee);
        projectInvitationRepository.delete(projectInvitation);

        return new ApiResponse(Boolean.TRUE, "프로젝트 초대 삭제 성공");
    }

    @Transactional(readOnly = true)
    public List<ProjectInvitationReadInviteeResult> readAllInvitee(Long projectIdx, Long accountIdx) {
        final Project project = prepareProject(projectIdx);
        final Account inviter = prepareAccount(accountIdx);

        checkProjectMember(project, inviter);

        final List<ProjectInvitation> projectInvitationList = projectInvitationRepository.findAllByProjectAndInviter(project, inviter);

        return projectInvitationList.stream().map(ProjectInvitationReadInviteeResult::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectInvitationReadPendingResult> readAllPending(Long accountIdx) {
        final Account account = prepareAccount(accountIdx);
        final List<ProjectInvitation> projectInvitationList = projectInvitationRepository.findAllByInvitee(account);

        return projectInvitationList.stream().map(ProjectInvitationReadPendingResult::from).toList();
    }

    private void checkProjectMember(Project project, Account account) {
        if (!projectMemberQueryService.isProjectMember(project, account)) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 프로젝트 멤버가 아님 )");
        }
    }

    private ProjectInvitation prepareProjectInvitation(Project project, Account invitee) {
        return projectInvitationRepository.findByProjectAndInvitee(project, invitee)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 프로젝트 초대입니다. projectId: " + project.getId() + ", inviteeId: " + invitee.getIdx()));
    }

    private Account prepareAccount(Long inviteeIdx) {
        return accountQueryService.findByIdx(inviteeIdx).orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다. idx: " + inviteeIdx));
    }

    private Project prepareProject(Long projectIdx) {
        return projectQueryService.findById(projectIdx).orElseThrow(() -> new BadRequestException("존재하지 않는 프로젝트입니다. idx: " + projectIdx));
    }
}