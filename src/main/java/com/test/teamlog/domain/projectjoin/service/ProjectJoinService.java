package com.test.teamlog.domain.projectjoin.service;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.project.service.query.ProjectQueryService;
import com.test.teamlog.domain.projectjoin.dto.ProjectJoinApplyInput;
import com.test.teamlog.domain.projectjoin.dto.ProjectJoinForProject;
import com.test.teamlog.domain.projectjoin.dto.ProjectJoinForAccount;
import com.test.teamlog.domain.projectjoin.dto.ProjectJoinInviteInput;
import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import com.test.teamlog.domain.projectjoin.repository.ProjectJoinRepository;
import com.test.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.exception.ResourceAlreadyExistsException;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectJoinService {
    private final ProjectJoinRepository projectJoinRepository;

    private final AccountQueryService accountQueryService;
    private final ProjectQueryService projectQueryService;
    private final ProjectMemberQueryService projectMemberQueryService;

    private String[] defaultProjectImages = new String[]{"20210504(81931d0a-14c3-43bd-912d-c4bd687c31ea)",
            "20210504(97a31008-24f4-4dc0-98bd-c83cf8d57b95)",
            "20210504(171eb9ac-f7ce-4e30-b4c6-a19a28e45c75)",
            "20210504(31157ace-269d-4a84-a73a-7a584f91ad9f)"};

    // 프로젝트 멤버 초대
    @Deprecated
    @Transactional
    public ApiResponse inviteAccountForProject(Long projectId, String accountId) {
        Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Account account = accountQueryService.findByIdentification(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACCOUNT", "id", accountId));

        if (projectMemberQueryService.isProjectMember(project, account))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트의 멤버입니다.");

        if (isJoinAlreadyExist(project, account))
            throw new ResourceAlreadyExistsException("해당 프로젝트의 멤버 신청 혹은 초대가 존재합니다.");

        ProjectJoin projectJoin = ProjectJoin.builder()
                .project(project)
                .account(account)
                .isAccepted(Boolean.FALSE)
                .isInvited(Boolean.TRUE)
                .build();
        projectJoinRepository.save(projectJoin);

        return new ApiResponse(Boolean.TRUE, "유저 : " + account.getName() + " 초대 완료");
    }

    @Transactional
    public ApiResponse invite(ProjectJoinInviteInput input, Account account) {
        final Project project = findProjectById(input.getProjectId());

        if (!projectMemberQueryService.isProjectMember(project, account))
            throw new ResourceAlreadyExistsException("프로젝트 멤버 초대 권한이 없습니다.");

        final List<ProjectJoin> projectJoinList = projectJoinRepository.findAllByProject(project);
        final Map<String, ProjectJoin> identificationToProjectJoinMap = projectJoinList.stream().collect(Collectors.toMap(pj -> pj.getAccount().getIdentification(), Function.identity()));

        final List<Account> accountList = accountQueryService.findAllByIdentificationIn(input.getAccountIdentificationList());
        final Map<String, Account> identificationToAccountMap
                = accountList.stream().collect(Collectors.toMap(Account::getIdentification, Function.identity()));

        List<ProjectJoin> newProjectJoinList = new ArrayList<>();
        List<String> notFoundaccountIdentificationList = new ArrayList<>();
        for (String accountIdentification : input.getAccountIdentificationList()) {
            if (!identificationToAccountMap.containsKey(accountIdentification)) {
                notFoundaccountIdentificationList.add(accountIdentification);
                continue;
            }

            if (!identificationToProjectJoinMap.containsKey(accountIdentification)) {
                newProjectJoinList.add(ProjectJoin.createInvitation(project, identificationToAccountMap.get(accountIdentification)));
            } else {
                final ProjectJoin projectJoin = identificationToProjectJoinMap.get(accountIdentification);
                projectJoin.update(true, projectJoin.getIsApplied());
            }
        }

        if (CollectionUtils.isEmpty(notFoundaccountIdentificationList)) {
            log.warn("존재하지 않는 사용자 아이디가 포함되어 있습니다. : {}", notFoundaccountIdentificationList);
        }

        projectJoinRepository.saveAll(newProjectJoinList);
        return new ApiResponse(Boolean.TRUE, "초대 완료");
    }


    // 프로젝트 멤버 신청
    @Deprecated
    @Transactional
    public ApiResponse applyForProjectV1(Long projectId, Account currentAccount) {
        Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (projectMemberQueryService.isProjectMember(project, currentAccount))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트의 멤버입니다.");

        if (isJoinAlreadyExist(project, currentAccount))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트에 멤버 신청 혹은 초대가 존재합니다.");

        ProjectJoin projectJoin = ProjectJoin.builder()
                .project(project)
                .account(currentAccount)
                .isAccepted(Boolean.TRUE)
                .isInvited(Boolean.FALSE)
                .build();
        projectJoinRepository.save(projectJoin);

        return new ApiResponse(Boolean.TRUE, "프로젝트 멤버 초대 완료");
    }

    public ApiResponse apply(ProjectJoinApplyInput input, Account currentAccount) {
        final Project project = findProjectById(input.getProjectId());

        if (projectMemberQueryService.isProjectMember(project, currentAccount)) {
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트의 멤버입니다.");
        }

        final ProjectJoin projectJoin = projectJoinRepository.findByProjectAndAccount(project, currentAccount).orElse(null);
        if (projectJoin == null) {
            projectJoinRepository.save(ProjectJoin.createApplication(project, currentAccount));
            return new ApiResponse(Boolean.TRUE, "프로젝트 멤버 신청 완료");
        }

        if (projectJoin.getIsAccepted()) {
            log.warn("이미 수락되었지만 프로젝트에 존재하지 않습니다. projectId: ({}), accountIdentification: ({})", input.getProjectId(), currentAccount.getIdentification());
        }

        projectJoin.update(projectJoin.getIsInvited(), true);
        return new ApiResponse(Boolean.TRUE, "프로젝트 멤버 신청 완료");
    }

    // 프로젝트 멤버 신청 삭제
    @Transactional
    public ApiResponse deleteProjectJoin(Long projectJoinId) {
        ProjectJoin projectJoin = projectJoinRepository.findById(projectJoinId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectJoin", "id", projectJoinId));

        projectJoinRepository.delete(projectJoin);

        return new ApiResponse(Boolean.TRUE, "프로젝트 멤버 신청 삭제 완료");
    }

    // 프로젝트 가입 신청자 목록 조회
    public List<ProjectJoinForProject> getProjectApplyListForProject(Long projectId) {
        Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        List<ProjectJoin> projectJoinList = projectJoinRepository.findAllByProjectAndIsAcceptedTrueAndIsInvitedFalse(project);
        return projectJoinList.stream().map(ProjectJoinForProject::from).toList();
    }

    // 프로젝트 멤버로 초대한 사용자 목록 조회
    public List<ProjectJoinForProject> getProjectInvitationListForProject(Long projectId) {
        Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        List<ProjectJoin> projectJoinList = projectJoinRepository.findAllByProjectAndIsAcceptedFalseAndIsInvitedTrue(project);
        return projectJoinList.stream().map(ProjectJoinForProject::from).toList();
    }

    // 유저가 받은 프로젝트 초대 조회
    public List<ProjectJoinForAccount> getProjectInvitationListForAccount(Account currentAccount) {
        List<ProjectJoin> projectJoins = projectJoinRepository.findAllByAccountAndIsAcceptedFalseAndIsInvitedTrue(currentAccount);

        List<ProjectJoinForAccount> response = new ArrayList<>();
        for (ProjectJoin join : projectJoins) {
            ProjectJoinForAccount temp = ProjectJoinForAccount.from(join);
            String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/resources/")
                    .path(defaultProjectImages[join.getProject().getId().intValue() % 4])
                    .toUriString();
            temp.setThumbnail(imgUri);
            response.add(temp);
        }

        return response;
    }

    // 유저가 가입 신청한 프로젝트 조회
    public List<ProjectJoinForAccount> getProjectApplyListForAccount(Account currentAccount) {
        List<ProjectJoin> projectJoins = projectJoinRepository.findAllByAccountAndIsAcceptedTrueAndIsInvitedFalse(currentAccount);

        List<ProjectJoinForAccount> response = new ArrayList<>();
        for (ProjectJoin join : projectJoins) {
            ProjectJoinForAccount temp = ProjectJoinForAccount.from(join);
            String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/resources/")
                    .path(defaultProjectImages[join.getProject().getId().intValue() % 4])
                    .toUriString();
            temp.setThumbnail(imgUri);
            response.add(temp);
        }

        return response;
    }

    // 이미 ProjectJoin 있을 경우
    public Boolean isJoinAlreadyExist(Project project, Account currentAccount) {
        return projectJoinRepository.findByProjectAndAccount(project, currentAccount).isPresent();
    }

    private Project findProjectById(Long projectId) {
        return projectQueryService.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));
    }
}
