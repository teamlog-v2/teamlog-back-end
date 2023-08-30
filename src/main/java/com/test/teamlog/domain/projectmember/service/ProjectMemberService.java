package com.test.teamlog.domain.projectmember.service;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.repository.AccountRepository;
import com.test.teamlog.domain.post.repository.PostRepository;
import com.test.teamlog.domain.project.service.ProjectService;
import com.test.teamlog.domain.projectmember.dto.ProjectMemberReadResult;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import com.test.teamlog.domain.projectmember.entity.ProjectMember;
import com.test.teamlog.exception.BadRequestException;
import com.test.teamlog.exception.ResourceForbiddenException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.domain.projectjoin.repository.ProjectJoinRepository;
import com.test.teamlog.domain.projectmember.repository.ProjectMemberRepository;
import com.test.teamlog.domain.project.repository.ProjectRepository;
import com.test.teamlog.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectMemberService {
    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final PostRepository postRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectJoinRepository projectJoinRepository;
    private final ProjectService projectService;
    private final FileStorageService fileStorageService;

    // 프로젝트 멤버 추가
    @Transactional
    public ApiResponse create(Long projectId, User currentUser) {
        final Project project = projectService.findOne(projectId);
        ProjectJoin projectJoin = projectJoinRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectInvitation", "ID", currentUser.getIdentification()));

        // TODO: project_join과 함께 다시 생각해보기
        if (!projectJoin.getIsInvited() || projectJoin.getIsAccepted()) throw new BadRequestException("잘못된 요청입니다.");

        projectMemberRepository.save(ProjectMember.create(projectJoin.getProject(), projectJoin.getUser()));
        return new ApiResponse(Boolean.TRUE, "프로젝트 멤버 가입 됨");
    }

    // 프로젝트 나가기
    @Transactional
    public ApiResponse leaveProject(Long projectId, User currentUser) {
        final Project project = projectService.findOne(projectId);

        if(project.getMaster().getIdentification().equals(currentUser.getIdentification())) {
            throw new ResourceForbiddenException("마스터는 탈퇴할 수 없습니다.\n위임하고 다시 시도하세요.");
        }

        ProjectMember projectMember = projectMemberRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMemeber", "UserId", currentUser.getIdentification()));
        projectMemberRepository.delete(projectMember);

        return new ApiResponse(Boolean.TRUE, "프로젝트 탈퇴 완료");
    }

    // 마스터 - 프로젝트 멤버 삭제
    @Transactional
    public ApiResponse expelMember(Long projectId, String userId, User currentUser) {
        final Project project = projectService.findOne(projectId);
        User user = accountRepository.findByIdentification(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        projectService.validateMasterUser(project, currentUser);

        ProjectMember projectMember = projectMemberRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMemeber", "UserId", userId));

        projectMemberRepository.delete(projectMember);
        return new ApiResponse(Boolean.TRUE, "멤버 삭제 완료");
    }


    // 프로젝트 멤버 조회
    public List<ProjectMemberReadResult> readAll(Long projectId) {
        final Project project = projectService.findOne(projectId);
        List<ProjectMember> projectMemberList = projectMemberRepository.findByProject(project);

        return projectMemberList.stream().map(ProjectMemberReadResult::of).toList();
    }


    // 프로젝트 멤버 아닌 유저 리스트
    @Deprecated
    public List<UserRequest.UserSimpleInfo> readAllNotInProjectMember(Long projectId) {
        List<User> userList = accountRepository.getUsersNotInProjectMember(projectId);
        List<UserRequest.UserSimpleInfo> response = new ArrayList<>();
        for (User user : userList) {
            response.add(new UserRequest.UserSimpleInfo(user));
        }
        return response;
    }
}
