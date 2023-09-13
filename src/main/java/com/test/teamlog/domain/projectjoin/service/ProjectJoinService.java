package com.test.teamlog.domain.projectjoin.service;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.service.AccountService;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.project.repository.ProjectRepository;
import com.test.teamlog.domain.project.service.ProjectService;
import com.test.teamlog.domain.projectjoin.dto.ProjectJoinApplyInput;
import com.test.teamlog.domain.projectjoin.dto.ProjectJoinInviteInput;
import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import com.test.teamlog.domain.projectjoin.repository.ProjectJoinRepository;
import com.test.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectJoinDTO;
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
    private final AccountService accountService;
    private final ProjectMemberQueryService projectMemberQueryService;
    
    private final AccountQueryService accountQueryService;
    private final ProjectRepository projectRepository;
    private final ProjectJoinRepository projectJoinRepository;
    private final ProjectService projectService;
    private String[] defaultProjectImages = new String[]{"20210504(81931d0a-14c3-43bd-912d-c4bd687c31ea)",
            "20210504(97a31008-24f4-4dc0-98bd-c83cf8d57b95)",
            "20210504(171eb9ac-f7ce-4e30-b4c6-a19a28e45c75)",
            "20210504(31157ace-269d-4a84-a73a-7a584f91ad9f)"};

    // 프로젝트 멤버 초대
    @Deprecated
    @Transactional
    public ApiResponse inviteUserForProject(Long projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        User user = accountQueryService.findByIdentification(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (projectMemberQueryService.isProjectMember(project, user))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트의 멤버입니다.");

        if (isJoinAlreadyExist(project, user))
            throw new ResourceAlreadyExistsException("해당 프로젝트의 멤버 신청 혹은 초대가 존재합니다.");

        ProjectJoin projectJoin = ProjectJoin.builder()
                .project(project)
                .user(user)
                .isAccepted(Boolean.FALSE)
                .isInvited(Boolean.TRUE)
                .build();
        projectJoinRepository.save(projectJoin);

        return new ApiResponse(Boolean.TRUE, "유저 : " + user.getName() + " 초대 완료");
    }

    @Transactional
    public ApiResponse inviteUserList(ProjectJoinInviteInput input, User user) {
        final Project project = projectService.findOne(input.getProjectId());

        if (!projectMemberQueryService.isProjectMember(project, user))
            throw new ResourceAlreadyExistsException("프로젝트 멤버 초대 권한이 없습니다.");

        final List<ProjectJoin> projectJoinList = projectJoinRepository.findAllByProject(project);
        final Map<String, ProjectJoin> identificationToProjectJoinMap = projectJoinList.stream().collect(Collectors.toMap(pj -> pj.getUser().getIdentification(), Function.identity()));

        final List<User> userList = accountService.readAllByIdentificationIn(input.getUserIdentificationList());
        final Map<String, User> identificationToUserMap
                = userList.stream().collect(Collectors.toMap(User::getIdentification, Function.identity()));

        List<ProjectJoin> newProjectJoinList = new ArrayList<>();
        List<String> notFoundUserIdentificationList = new ArrayList<>();
        for (String userIdentification : input.getUserIdentificationList()) {
            if (!identificationToUserMap.containsKey(userIdentification)) {
                notFoundUserIdentificationList.add(userIdentification);
                continue;
            }

            if (!identificationToProjectJoinMap.containsKey(userIdentification)) {
                newProjectJoinList.add(ProjectJoin.createInvitation(project, identificationToUserMap.get(userIdentification)));
            } else {
                final ProjectJoin projectJoin = identificationToProjectJoinMap.get(userIdentification);
                projectJoin.update(true, projectJoin.getIsApplied());
            }
        }

        if (CollectionUtils.isEmpty(notFoundUserIdentificationList)) {
            log.warn("존재하지 않는 사용자 아이디가 포함되어 있습니다. : {}", notFoundUserIdentificationList);
        }

        projectJoinRepository.saveAll(newProjectJoinList);
        return new ApiResponse(Boolean.TRUE, "초대 완료");
    }


    // 프로젝트 멤버 신청
    @Deprecated
    @Transactional
    public ApiResponse applyForProjectV1(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (projectMemberQueryService.isProjectMember(project, currentUser))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트의 멤버입니다.");

        if (isJoinAlreadyExist(project, currentUser))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트에 멤버 신청 혹은 초대가 존재합니다.");

        ProjectJoin projectJoin = ProjectJoin.builder()
                .project(project)
                .user(currentUser)
                .isAccepted(Boolean.TRUE)
                .isInvited(Boolean.FALSE)
                .build();
        projectJoinRepository.save(projectJoin);

        return new ApiResponse(Boolean.TRUE, "프로젝트 멤버 초대 완료");
    }

    public ApiResponse apply(ProjectJoinApplyInput input, User currentUser) {
        final Project project = projectService.findOne(input.getProjectId());

        if (projectMemberQueryService.isProjectMember(project, currentUser)) {
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트의 멤버입니다.");
        }

        final ProjectJoin projectJoin = projectJoinRepository.findByProjectAndUser(project, currentUser).orElse(null);
        if (projectJoin == null) {
            projectJoinRepository.save(ProjectJoin.createApplication(project, currentUser));
            return new ApiResponse(Boolean.TRUE, "프로젝트 멤버 신청 완료");
        }

        if (projectJoin.getIsAccepted()) {
            log.warn("이미 수락되었지만 프로젝트에 존재하지 않습니다. projectId: ({}), userIdentification: ({})", input.getProjectId(), currentUser.getIdentification());
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
    public List<ProjectJoinDTO.ProjectJoinForProject> getProjectApplyListForProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        List<ProjectJoin> projectJoins = projectJoinRepository.findAllByProjectAndIsAcceptedTrueAndIsInvitedFalse(project);

        List<ProjectJoinDTO.ProjectJoinForProject> response = new ArrayList<>();
        for (ProjectJoin join : projectJoins) {
            UserRequest.UserSimpleInfo user = new UserRequest.UserSimpleInfo(join.getUser());
            ProjectJoinDTO.ProjectJoinForProject temp = ProjectJoinDTO.ProjectJoinForProject.builder()
                    .id(join.getId())
                    .projectName(join.getProject().getName())
                    .user(user)
                    .build();
            response.add(temp);
        }
        return response;
    }

    // 프로젝트 멤버로 초대한 사용자 목록 조회
    public List<ProjectJoinDTO.ProjectJoinForProject> getProjectInvitationListForProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        List<ProjectJoin> projectJoins = projectJoinRepository.findAllByProjectAndIsAcceptedFalseAndIsInvitedTrue(project);

        List<ProjectJoinDTO.ProjectJoinForProject> response = new ArrayList<>();
        for (ProjectJoin join : projectJoins) {
            UserRequest.UserSimpleInfo user = new UserRequest.UserSimpleInfo(join.getUser());
            ProjectJoinDTO.ProjectJoinForProject temp = ProjectJoinDTO.ProjectJoinForProject.builder()
                    .id(join.getId())
                    .projectName(join.getProject().getName())
                    .user(user)
                    .build();
            response.add(temp);
        }
        return response;
    }

    // 유저가 받은 프로젝트 초대 조회
    public List<ProjectJoinDTO.ProjectJoinForUser> getProjectInvitationListForUser(User currentUser) {
        List<ProjectJoin> projectJoins = projectJoinRepository.findAllByUserAndIsAcceptedFalseAndIsInvitedTrue(currentUser);

        List<ProjectJoinDTO.ProjectJoinForUser> response = new ArrayList<>();
        for (ProjectJoin join : projectJoins) {
            ProjectJoinDTO.ProjectJoinForUser temp = ProjectJoinDTO.ProjectJoinForUser.builder()
                    .id(join.getId())
                    .projectId(join.getProject().getId())
                    .projectName(join.getProject().getName())
                    .build();
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
    public List<ProjectJoinDTO.ProjectJoinForUser> getProjectApplyListForUser(User currentUser) {
        List<ProjectJoin> projectJoins = projectJoinRepository.findAllByUserAndIsAcceptedTrueAndIsInvitedFalse(currentUser);

        List<ProjectJoinDTO.ProjectJoinForUser> response = new ArrayList<>();
        for (ProjectJoin join : projectJoins) {
            ProjectJoinDTO.ProjectJoinForUser temp = ProjectJoinDTO.ProjectJoinForUser.builder()
                    .id(join.getId())
                    .projectId(join.getProject().getId())
                    .projectName(join.getProject().getName())
                    .build();
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
    public Boolean isJoinAlreadyExist(Project project, User currentUser) {
        return projectJoinRepository.findByProjectAndUser(project, currentUser).isPresent();
    }
}
