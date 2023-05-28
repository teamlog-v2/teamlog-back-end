package com.test.teamlog.service;

import com.test.teamlog.domain.account.model.User;

import com.test.teamlog.domain.account.repository.UserRepository;
import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.ProjectJoin;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectJoinDTO;
import com.test.teamlog.domain.account.dto.UserDTO;
import com.test.teamlog.repository.ProjectJoinRepository;
import com.test.teamlog.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectJoinService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectJoinRepository projectJoinRepository;
    private final ProjectService projectService;
    private String[] defaultProjectImages = new String[]{"20210504(81931d0a-14c3-43bd-912d-c4bd687c31ea)",
            "20210504(97a31008-24f4-4dc0-98bd-c83cf8d57b95)",
            "20210504(171eb9ac-f7ce-4e30-b4c6-a19a28e45c75)",
            "20210504(31157ace-269d-4a84-a73a-7a584f91ad9f)"};

    // 프로젝트 멤버 초대
    @Transactional
    public ApiResponse inviteUserForProject(Long projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (projectService.isUserMemberOfProject(project, user))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트의 멤버입니다.");

        if (projectService.isJoinAlreadyExist(project, user))
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

    // 프로젝트 멤버 신청
    @Transactional
    public ApiResponse applyForProject(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (projectService.isUserMemberOfProject(project, currentUser))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트의 멤버입니다.");

        if (projectService.isJoinAlreadyExist(project, currentUser))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트에 멤버 신청 혹은 초대가 존재합니다.");

        ProjectJoin projectJoin = ProjectJoin.builder()
                .project(project)
                .user(currentUser)
                .isAccepted(Boolean.TRUE)
                .isInvited(Boolean.FALSE)
                .build();
        projectJoinRepository.save(projectJoin);

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
            UserDTO.UserSimpleInfo user = new UserDTO.UserSimpleInfo(join.getUser());
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
            UserDTO.UserSimpleInfo user = new UserDTO.UserSimpleInfo(join.getUser());
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
}
