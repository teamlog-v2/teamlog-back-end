package com.test.teamlog.domain.projectinvitation.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.project.service.query.ProjectQueryService;
import com.test.teamlog.domain.projectinvitation.dto.*;
import com.test.teamlog.domain.projectinvitation.entity.ProjectInvitation;
import com.test.teamlog.domain.projectinvitation.repository.ProjectInvitationRepository;
import com.test.teamlog.domain.projectmember.entity.ProjectMember;
import com.test.teamlog.domain.projectmember.service.command.ProjectMemberCommandService;
import com.test.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.exception.ResourceAlreadyExistsException;
import com.test.teamlog.global.exception.ResourceForbiddenException;
import com.test.teamlog.global.exception.ResourceNotFoundException;
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
                = readProjectByIdx(projectIdx);
        final User inviter
                = readUserByIdx(inviterIdx);
        final User invitee
                = readUserByIdx(inviteeIdx);

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
                = readProjectByIdx(projectIdx);
        final User invitee
                = readUserByIdx(inviteeIdx);

        final ProjectInvitation projectInvitation = projectInvitationRepository.findByProjectAndInvitee(project, invitee)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", projectIdx));

        if (projectInvitation == null) {
            throw new ResourceNotFoundException("ProjectInvitation", "ProjectIdx, InviteeIdx", projectIdx + ", " + inviteeIdx);
        }

        if (projectMemberQueryService.isProjectMember(project, invitee)) {
            throw new ResourceAlreadyExistsException("이미 프로젝트 멤버입니다.");
        }

        projectMemberCommandService.save(ProjectMember.create(project, invitee));
        projectInvitationRepository.delete(projectInvitation);

        return new ApiResponse(Boolean.TRUE, "프로젝트 초대 수락 성공");
    }

    @Transactional
    public ApiResponse reject(ProjectInvitationRejectInput input) {
        final Long projectIdx = input.getProjectIdx();
        final Long inviteeIdx = input.getInviteeIdx();

        final Project project
                = readProjectByIdx(projectIdx);
        final User invitee
                = readUserByIdx(inviteeIdx);

        final ProjectInvitation projectInvitation = projectInvitationRepository.findByProjectAndInvitee(project, invitee)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", projectIdx));

        projectInvitationRepository.delete(projectInvitation);

        return new ApiResponse(Boolean.TRUE, "프로젝트 초대 거절 성공");
    }

    @Transactional
    public ApiResponse cancel(ProjectInvitationCancelInput input) {
        final Long projectIdx = input.getProjectIdx();
        final Long inviterIdx = input.getInviterIdx();
        final Long inviteeIdx = input.getInviteeIdx();

        final Project project
                = readProjectByIdx(projectIdx);
        final User inviter
                = readUserByIdx(inviterIdx);
        final User invitee
                = readUserByIdx(inviteeIdx);

        final ProjectInvitation projectInvitation = projectInvitationRepository.findByProjectAndInvitee(project, invitee)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", projectIdx));

        if (!projectMemberQueryService.isProjectMember(project, inviter)) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 프로젝트 멤버가 아님 )");
        }

        projectInvitationRepository.delete(projectInvitation);

        return new ApiResponse(Boolean.TRUE, "프로젝트 초대 삭제 성공");
    }

    @Transactional(readOnly = true)
    public List<ProjectInvitationReadInviteeResult> readAllInvitee(Long projectIdx, Long userIdx) {
        final Project project
                = readProjectByIdx(projectIdx);
        final User inviter
                = readUserByIdx(userIdx);

        if (!projectMemberQueryService.isProjectMember(project, inviter)) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 프로젝트 멤버가 아님 )");
        }

        final List<ProjectInvitation> projectInvitationList = projectInvitationRepository.findAllByProjectAndInviter(project, inviter);
        return projectInvitationList.stream().map(ProjectInvitationReadInviteeResult::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectInvitationReadPendingResult> readAllPending(Long userIdx) {
        final User user
                = readUserByIdx(userIdx);

        final List<ProjectInvitation> projectInvitationList = projectInvitationRepository.findAllByInvitee(user);
        return projectInvitationList.stream().map(ProjectInvitationReadPendingResult::from).toList();
    }

    private User readUserByIdx(Long inviteeIdx) {
        return accountQueryService.findByIdx(inviteeIdx).orElseThrow(() -> new ResourceNotFoundException("User", "ID", inviteeIdx));
    }

    private Project readProjectByIdx(Long projectIdx) {
        return projectQueryService.findById(projectIdx).orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectIdx));
    }
}