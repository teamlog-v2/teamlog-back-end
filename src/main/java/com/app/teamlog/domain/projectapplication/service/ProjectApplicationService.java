package com.app.teamlog.domain.projectapplication.service;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.account.service.query.AccountQueryService;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.project.service.query.ProjectQueryService;
import com.app.teamlog.domain.projectapplication.dto.ProjectApplicationAcceptInput;
import com.app.teamlog.domain.projectapplication.dto.ProjectApplicationCreateInput;
import com.app.teamlog.domain.projectapplication.dto.ProjectApplicationReadApplicantsResult;
import com.app.teamlog.domain.projectapplication.dto.ProjectApplicationReadPendingResult;
import com.app.teamlog.domain.projectapplication.entity.ProjectApplication;
import com.app.teamlog.domain.projectapplication.repository.ProjectApplicationRepository;
import com.app.teamlog.domain.projectmember.entity.ProjectMember;
import com.app.teamlog.domain.projectmember.service.command.ProjectMemberCommandService;
import com.app.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.app.teamlog.global.dto.ApiResponse;
import com.app.teamlog.global.exception.BadRequestException;
import com.app.teamlog.global.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectApplicationService {
    private final ProjectApplicationRepository projectApplicationRepository;

    private final ProjectQueryService projectQueryService;
    private final AccountQueryService accountQueryService;
    private final ProjectMemberQueryService projectMemberQueryService;
    private final ProjectMemberCommandService projectMemberCommandService;

    @Transactional
    public ApiResponse create(ProjectApplicationCreateInput input) {
        final Long projectIdx = input.getProjectIdx();
        final Long applicantIdx = input.getApplicantIdx();

        final Project project = prepareProject(projectIdx);
        final Account applicant = prepareAccount(applicantIdx);

        // 프로젝트 멤버 여부 확인
        if (projectMemberQueryService.isProjectMember(project, applicant)) {
            throw new BadRequestException("이미 프로젝트 멤버입니다.");
        }

        final ProjectApplication projectApplication = projectApplicationRepository.findByProjectAndApplicant(project, applicant).orElse(null);

        if (projectApplication != null) {
            throw new ResourceAlreadyExistsException("이미 존재하는 프로젝트 참여 신청입니다.");
        }

        projectApplicationRepository.save(input.toProjectApplication(project, applicant));

        return new ApiResponse(Boolean.TRUE, "프로젝트 참여 신청 성공");
    }

    @Transactional
    public ApiResponse accept(ProjectApplicationAcceptInput input) {
        final ProjectApplication projectApplication = prepareProjectApplication(input.getApplicationIdx());

        final Project project = projectApplication.getProject();
        final Account account = prepareAccount(input.getAccountIdx());

        final Account applicant = projectApplication.getApplicant();

        // 요청 수락자가 프로젝트 멤버인지 확인
        projectMemberQueryService.findByProjectAndUAccount(project, account).orElseThrow(() -> new BadRequestException("프로젝트 멤버가 아닙니다."));

        // 요청자가 이미 프로젝트 멤버인지 확인
        projectMemberQueryService.findByProjectAndUAccount(project, applicant)
                .ifPresent(projectMember -> {
                    throw new BadRequestException("이미 프로젝트 멤버입니다.");
                });

        projectMemberCommandService.save(ProjectMember.create(project, account));
        projectApplicationRepository.delete(projectApplication);

        return new ApiResponse(Boolean.TRUE, "프로젝트 신청 수락 성공");
    }

    @Transactional
    public ApiResponse reject(Long projectApplicationIdx, Long accountIdx) {
        final ProjectApplication projectApplication = prepareProjectApplication(projectApplicationIdx);

        final Project project = projectApplication.getProject();
        final Account account = prepareAccount(accountIdx);

        projectMemberQueryService.findByProjectAndUAccount(project, account).orElseThrow(() -> new BadRequestException("프로젝트 멤버가 아닙니다."));
        projectApplicationRepository.delete(projectApplication);

        return new ApiResponse(Boolean.TRUE, "프로젝트 신청 거절 성공");
    }

    @Transactional
    public ApiResponse cancel(Long projectApplicationIdx, Long accountIdx) {
        final ProjectApplication projectApplication = prepareProjectApplication(projectApplicationIdx);

        if (!accountIdx.equals(projectApplication.getApplicant().getIdx())) throw new BadRequestException("권한이 없습니다.");

        projectApplicationRepository.delete(projectApplication);

        return new ApiResponse(Boolean.TRUE, "프로젝트 신청 취소 성공");
    }

    @Transactional(readOnly = true)
    public List<ProjectApplicationReadApplicantsResult> readAllApplicants(Long projectIdx, Long accountIdx) {
        final Project project = prepareProject(projectIdx);
        final Account account = prepareAccount(accountIdx);

        if (!projectMemberQueryService.isProjectMember(project, account))
            throw new BadRequestException("프로젝트 멤버가 아닙니다.");

        final List<ProjectApplication> projectApplicationList = projectApplicationRepository.findAllByProject(project);
        return projectApplicationList.stream().map(ProjectApplicationReadApplicantsResult::from).toList();
    }

    public List<ProjectApplicationReadPendingResult> readAllPending(Long accountIdx) {
        final Account account = prepareAccount(accountIdx);

        final List<ProjectApplication> projectApplicationList = projectApplicationRepository.findAllByApplicant(account);
        return projectApplicationList.stream().map(ProjectApplicationReadPendingResult::from).toList();
    }

    private ProjectApplication prepareProjectApplication(Long projectApplicationIdx) {
        return projectApplicationRepository.findById(projectApplicationIdx).orElseThrow(() -> new BadRequestException("ProjectApplication"));
    }

    private Project prepareProject(Long projectIdx) {
        return projectQueryService.findById(projectIdx).orElseThrow(() -> new BadRequestException("Project"));
    }

    private Account prepareAccount(Long accountIdx) {
        return accountQueryService.findByIdx(accountIdx).orElseThrow(() -> new BadRequestException("Account"));
    }
}
