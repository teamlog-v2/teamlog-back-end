package com.test.teamlog.service;

import com.test.teamlog.domain.account.model.User;

import com.test.teamlog.domain.account.repository.AccountRepository;
import com.test.teamlog.entity.*;
import com.test.teamlog.exception.BadRequestException;
import com.test.teamlog.exception.ResourceForbiddenException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.domain.post.repository.PostRepository;
import com.test.teamlog.repository.ProjectJoinRepository;
import com.test.teamlog.repository.ProjectMemberRepository;
import com.test.teamlog.repository.ProjectRepository;
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

    // 프로젝트 멤버 아닌 유저 리스트
    public List<UserRequest.UserSimpleInfo> getUsersNotInProjectMember(Long projectId) {
        List<User> userList = accountRepository.getUsersNotInProjectMember(projectId);
        List<UserRequest.UserSimpleInfo> response = new ArrayList<>();
        for (User user : userList) {
            response.add(new UserRequest.UserSimpleInfo(user));
        }
        return response;
    }

    // 프로젝트 멤버 추가 ( 초대 수락 )
    @Transactional
    public ApiResponse createProjectMember(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));
        ProjectJoin join = projectJoinRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectInvitation", "ID", currentUser.getIdentification()));
        if (join.getIsInvited() != true || join.getIsAccepted() != false) throw new BadRequestException("잘못된 요청입니다.");
        projectJoinRepository.delete(join);

        ProjectMember newMember = ProjectMember.builder()
                .project(join.getProject())
                .user(join.getUser())
                .build();
        projectMemberRepository.save(newMember);
        return new ApiResponse(Boolean.TRUE, "프로젝트 멤버 가입 됨");
    }

    // 프로젝트 멤버 추가
    @Transactional
    public ApiResponse acceptProjectInvitation(Long id) {
        ProjectJoin join = projectJoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectInvitation", "ID", id));
        // TODO : 수락하는 사람이 마스터이냐 사용자이냐에 따라 구분해야함.
        projectJoinRepository.delete(join);

        ProjectMember newMember = ProjectMember.builder()
                .project(join.getProject())
                .user(join.getUser())
                .build();
        projectMemberRepository.save(newMember);
        return new ApiResponse(Boolean.TRUE, "프로젝트 멤버 추가");
    }

    // 프로젝트 멤버 조회
    public List<UserRequest.UserSimpleInfo> getProjectMemberList(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        List<ProjectMember> members = projectMemberRepository.findByProject(project);

        List<UserRequest.UserSimpleInfo> memberList = new ArrayList<>();
        for (ProjectMember member : members) {
            memberList.add(new UserRequest.UserSimpleInfo(member.getUser()));
        }

        return memberList;
    }

    // 프로젝트 나가기
    @Transactional
    public ApiResponse leaveProject(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        if(project.getMaster().getIdentification().equals(currentUser.getIdentification())) {
            throw new ResourceForbiddenException("마스터는 탈퇴할 수 없습니다.\n위임하고 다시 시도하세요.");
        }
        ProjectMember member = projectMemberRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMemeber", "UserId", currentUser.getIdentification()));

        List<Post> postList = postRepository.findAllByProjectAndWriter(project, currentUser);
        for(Post post : postList) {
            fileStorageService.deleteFilesByPost(post);
            postRepository.delete(post);
        }

        projectMemberRepository.delete(member);
        return new ApiResponse(Boolean.TRUE, "프로젝트 탈퇴 완료");
    }

    // 마스터 - 프로젝트 멤버 삭제
    @Transactional
    public ApiResponse expelMember(Long projectId, String userId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        User user = accountRepository.findByIdentification(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        projectService.validateUserIsMaster(project, currentUser);
        ProjectMember member = projectMemberRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMemeber", "UserId", userId));

        List<Post> postList = postRepository.findAllByProjectAndWriter(project,user);
        for(Post post : postList) {
            fileStorageService.deleteFilesByPost(post);
            postRepository.delete(post);
        }

        projectMemberRepository.delete(member);
        return new ApiResponse(Boolean.TRUE, "멤버 삭제 완료");
    }
}
