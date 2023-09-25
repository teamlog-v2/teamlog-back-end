package com.test.teamlog.domain.projectinvitation.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.project.service.query.ProjectQueryService;
import com.test.teamlog.domain.projectinvitation.dto.*;
import com.test.teamlog.domain.projectinvitation.entity.ProjectInvitation;
import com.test.teamlog.domain.projectinvitation.repository.ProjectInvitationRepository;
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

    @Transactional
    public ApiResponse create(ProjectInvitationCreateInput input) {
        final Long projectIdx = input.getProjectIdx();
        final Long inviterIdx = input.getInviterIdx();
        final Long inviteeIdx = input.getInviteeIdx();

        final Project project
                = projectQueryService.findById(projectIdx).orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectIdx));
        final User inviter
                = accountQueryService.findByIdx(inviterIdx).orElseThrow(() -> new ResourceNotFoundException("User", "ID", inviterIdx));
        final User invitee
                = accountQueryService.findByIdx(inviteeIdx).orElseThrow(() -> new ResourceNotFoundException("User", "ID", inviteeIdx));

        if (!projectMemberQueryService.isProjectMember(project, inviter)) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 프로젝트 멤버가 아님 )");
        }

        final ProjectInvitation projectInvitation = projectInvitationRepository.findByProjectAndInvitee(project, invitee).orElse(null);

        if (projectInvitation == null) {
            projectInvitationRepository.save(input.toProjectInvitation(project, inviter, invitee));
        } else {
            if (projectInvitation.isAccepted()) {
                throw new ResourceAlreadyExistsException("이미 수락하였습니다.");
            }

            projectInvitation.update();
        }

        return new ApiResponse(Boolean.TRUE, "프로젝트 초대 성공");
    }

    @Transactional
    public ApiResponse accept(ProjectInvitationAcceptInput input) {
        final Long projectIdx = input.getProjectIdx();
        final Long inviteeIdx = input.getInviteeIdx();

        final Project project
                = projectQueryService.findById(projectIdx).orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectIdx));
        final User invitee
                = accountQueryService.findByIdx(inviteeIdx).orElseThrow(() -> new ResourceNotFoundException("User", "ID", inviteeIdx));

        final ProjectInvitation projectInvitation = projectInvitationRepository.findByProjectAndInvitee(project, invitee)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", projectIdx));

        if (projectInvitation == null) {
            throw new ResourceNotFoundException("ProjectInvitation", "ProjectIdx, InviteeIdx", projectIdx + ", " + inviteeIdx);
        }

        if (projectInvitation.isAccepted()) {
            throw new ResourceAlreadyExistsException("이미 프로젝트 초대를 수락했습니다.");
        }

        projectInvitation.accept();

        return new ApiResponse(Boolean.TRUE, "프로젝트 초대 수락 성공");
    }

    @Transactional
    public ApiResponse delete(ProjectInvitationDeleteInput input) {
        final Long projectIdx = input.getProjectIdx();
        final Long inviterIdx = input.getInviterIdx();
        final Long inviteeIdx = input.getInviteeIdx();

        final Project project
                = projectQueryService.findById(projectIdx).orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectIdx));
        final User inviter
                = accountQueryService.findByIdx(inviterIdx).orElseThrow(() -> new ResourceNotFoundException("User", "ID", inviterIdx));
        final User invitee
                = accountQueryService.findByIdx(inviteeIdx).orElseThrow(() -> new ResourceNotFoundException("User", "ID", inviteeIdx));

        final ProjectInvitation projectInvitation = projectInvitationRepository.findByProjectAndInvitee(project, invitee)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", projectIdx));

        if (!projectMemberQueryService.isProjectMember(project, inviter)) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 프로젝트 멤버가 아님 )");
        }

        if (!projectInvitation.isAccepted()) {
            throw new ResourceAlreadyExistsException("이미 프로젝트 초대를 수락했습니다.");
        }

        projectInvitationRepository.delete(projectInvitation);

        return new ApiResponse(Boolean.TRUE, "프로젝트 초대 삭제 성공");
    }

    @Transactional(readOnly = true)
    public List<ProjectInvitationReadInviteeResult> readAllInvitee(Long projectIdx, Long userIdx) {
        final Project project
                = projectQueryService.findById(projectIdx).orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectIdx));
        final User inviter
                = accountQueryService.findByIdx(userIdx).orElseThrow(() -> new ResourceNotFoundException("User", "ID", userIdx));

        if (!projectMemberQueryService.isProjectMember(project, inviter)) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 프로젝트 멤버가 아님 )");
        }

        return projectInvitationRepository.findAllByProjectAndAcceptedIsFalse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectInvitationReadPendingResult> readAllPending(Long userIdx) {
        final User user
                = accountQueryService.findByIdx(userIdx).orElseThrow(() -> new ResourceNotFoundException("User", "ID", userIdx));

        return projectInvitationRepository.findAllByUserAndAcceptedIsFalse(user);
    }
}