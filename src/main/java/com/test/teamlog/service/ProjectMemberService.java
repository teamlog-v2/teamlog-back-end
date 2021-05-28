package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.BadRequestException;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceForbiddenException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.*;
import com.test.teamlog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectMemberService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectJoinRepository projectJoinRepository;
    private final ProjectService projectService;

    // 프로젝트 멤버 아닌 유저 리스트
    public List<UserDTO.UserSimpleInfo> getUsersNotInProjectMember(Long projectId) {
        List<User> userList = userRepository.getUsersNotInProjectMember(projectId);
        List<UserDTO.UserSimpleInfo> response = new ArrayList<>();
        for (User user : userList) {
            response.add(new UserDTO.UserSimpleInfo(user));
        }
        return response;
    }

    // ---------------------------
    // ----- 프로젝트 멤버 관리 -----
    // ---------------------------
    // 프로젝트 멤버 추가 ( 초대 수락 )
    @Transactional
    public ApiResponse createProjectMember(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));
        ProjectJoin join = projectJoinRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectInvitation", "ID", currentUser.getId()));
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
    public List<UserDTO.UserSimpleInfo> getProjectMemberList(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        List<ProjectMember> members = projectMemberRepository.findByProject(project);

        List<UserDTO.UserSimpleInfo> memberList = new ArrayList<>();
        for (ProjectMember member : members) {
            memberList.add(new UserDTO.UserSimpleInfo(member.getUser()));
        }

        return memberList;
    }

    // 프로젝트 나가기
    @Transactional
    public ApiResponse leaveProject(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        // TODO : 자기자신이 마스터면 나갈 수 없어야함.
        ProjectMember member = projectMemberRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMemeber", "UserId", currentUser.getId()));
        projectMemberRepository.delete(member);
        return new ApiResponse(Boolean.TRUE, "프로젝트 탈퇴 완료");
    }

    // 마스터 - 프로젝트 멤버 삭제
    @Transactional
    public ApiResponse expelMember(Long projectId, String userId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        projectService.validateUserIsMaster(project, currentUser);
        ProjectMember member = projectMemberRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMemeber", "UserId", userId));
        projectMemberRepository.delete(member);
        return new ApiResponse(Boolean.TRUE, "멤버 삭제 완료");
    }

    // member pk 까지 준다면 (마스터)
    @Transactional
    public ApiResponse deleteProjectMemeber(Long id) {
        ProjectMember member = projectMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMemeber", "id", id));
        projectMemberRepository.delete(member);
        return new ApiResponse(Boolean.TRUE, "멤버 삭제 완료");
    }

    // 프로젝트 멤버 검증
    public void validateUserIsMemberOfProject(Project project, User currentUser) {
        if (currentUser == null) throw new ResourceForbiddenException("권한이 없습니다. 로그인 해주세요.");
        projectMemberRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceForbiddenException("권한이 없습니다. ( 프로젝트 멤버 아님 )"));
    }
}
