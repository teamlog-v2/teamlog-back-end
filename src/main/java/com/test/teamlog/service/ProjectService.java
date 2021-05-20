package com.test.teamlog.service;

import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.ProjectJoin;
import com.test.teamlog.entity.ProjectMember;
import com.test.teamlog.entity.User;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceForbiddenException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.ProjectJoinDTO;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.ListIndexBase;
import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectJoinRepository projectJoinRepository;
    private final PostRepository postRepository;
    private String[] defaultProjectImages = new String[]{"20210504(81931d0a-14c3-43bd-912d-c4bd687c31ea)",
            "20210504(97a31008-24f4-4dc0-98bd-c83cf8d57b95)",
            "20210504(171eb9ac-f7ce-4e30-b4c6-a19a28e45c75)",
            "20210504(31157ace-269d-4a84-a73a-7a584f91ad9f)"};

    public ProjectDTO.Relation getRelation(Project project, User currentUser) {
        if(isUserMemberOfProject(project, currentUser)) {
            return ProjectDTO.Relation.MEMBER;
        }
        ProjectJoin join = projectJoinRepository.findByProjectAndUser(project, currentUser).orElse(null);
        if(join != null) {
            if(join.getIsAccepted() == true && join.getIsInvited() == false) {
                return ProjectDTO.Relation.APPLIED;
            }
            if(join.getIsAccepted() == false && join.getIsInvited() == true) {
                return ProjectDTO.Relation.INVITED;
            }
        }
        return ProjectDTO.Relation.NONE;
    }
    // 단일 프로젝트 조회
    public ProjectDTO.ProjectResponse getProject(Long id, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        ProjectDTO.ProjectResponse projectResponse = new ProjectDTO.ProjectResponse(project);
        projectResponse.setRelation(getRelation(project,currentUser));
        return projectResponse;
    }

    // 사용자 프로젝트 리스트 조회
    public List<ProjectDTO.ProjectListResponse> getProjectsByUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("USER", "id", id));

        List<ProjectMember> projectList = projectMemberRepository.findByUser(user);

        List<ProjectDTO.ProjectListResponse> projects = new ArrayList<>();
        for (ProjectMember project : projectList) {
            long postcount = postRepository.getPostsCount(project.getProject());

            String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/resources/")
                    .path(defaultProjectImages[project.getProject().getId().intValue() % 4])
                    .toUriString();
            System.out.println(imgUri);
            ProjectDTO.ProjectListResponse item = ProjectDTO.ProjectListResponse.builder()
                    .id(project.getProject().getId())
                    .name(project.getProject().getName())
                    .postCount(postcount)
                    .updateTime(project.getProject().getUpdateTime())
                    .thumbnail(imgUri)
                    .build();
            projects.add(item);
        }
        return projects;
    }

    // 프로젝트 생성
    @Transactional
    public ApiResponse createProject(ProjectDTO.ProjectRequest request, User currentUser) {
        Project project = Project.builder()
                .name(request.getName())
                .introduction(request.getIntroduction())
                .accessModifier(request.getAccessModifier())
                .master(currentUser)
                .build();
        projectRepository.save(project);

        ProjectMember member = ProjectMember.builder()
                .user(currentUser)
                .project(project)
                .build();
        projectMemberRepository.save(member);

        return new ApiResponse(Boolean.TRUE, "프로젝트 생성 성공");
    }

    // 프로젝트 수정 ( 위임 일단 포함 )
    @Transactional
    public ApiResponse updateProject(Long id, ProjectDTO.ProjectRequest request, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        validateUserIsMaster(project,currentUser);

        project.setName(request.getName());
        project.setIntroduction(request.getIntroduction());
        project.setAccessModifier(request.getAccessModifier());

        if (request.getMasterId() != null) {
            User newMaster = userRepository.findById(request.getMasterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getMasterId()));
            project.setMaster(newMaster);
        }
        projectRepository.save(project);

        return new ApiResponse(Boolean.TRUE, "프로젝트 수정 성공");
    }

    // 프로젝트 삭제
    @Transactional
    public ApiResponse deleteProject(Long id, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        validateUserIsMaster(project,currentUser);

        projectRepository.delete(project);
        return new ApiResponse(Boolean.TRUE, "프로젝트 삭제 성공");
    }

    // -------------------------------
    // ----- 프로젝트 멤버 신청 관리 -----
    // -------------------------------
    // 프로젝트 멤버 초대
    @Transactional
    public ApiResponse inviteUserForProject(Long projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if(isUserMemberOfProject(project,user))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트의 멤버입니다.");

        if(isJoinAlreadyExist(project,user))
            throw new ResourceAlreadyExistsException("해당 프로젝트의 멤버 신청 혹은 초대가 존재합니다.");

        ProjectJoin projectJoin = ProjectJoin.builder()
                .project(project)
                .user(user)
                .isAccepted(Boolean.TRUE)
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

        if(isUserMemberOfProject(project,currentUser))
            throw new ResourceAlreadyExistsException("이미 해당 프로젝트의 멤버입니다.");

        if(isJoinAlreadyExist(project,currentUser))
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
        for(ProjectJoin join : projectJoins) {
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
        for(ProjectJoin join : projectJoins) {
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
        for(ProjectJoin join : projectJoins) {
            ProjectJoinDTO.ProjectJoinForUser temp = ProjectJoinDTO.ProjectJoinForUser.builder()
                    .id(join.getId())
                    .projectName(join.getProject().getName())
                    .build();
            response.add(temp);
        }
        return response;
    }

    // 유저가 가입 신청한 프로젝트 조회
    public List<ProjectJoinDTO.ProjectJoinForUser> getProjectApplyListForUser(User currentUser) {
        List<ProjectJoin> projectJoins = projectJoinRepository.findAllByUserAndIsAcceptedTrueAndIsInvitedFalse(currentUser);

        List<ProjectJoinDTO.ProjectJoinForUser> response = new ArrayList<>();
        for(ProjectJoin join : projectJoins) {
            ProjectJoinDTO.ProjectJoinForUser temp = ProjectJoinDTO.ProjectJoinForUser.builder()
                    .id(join.getId())
                    .projectName(join.getProject().getName())
                    .build();
            response.add(temp);
        }
        return response;
    }

    // ---------------------------
    // ----- 프로젝트 멤버 관리 -----
    // ---------------------------
    // 프로젝트 멤버 추가
    @Transactional
    public ApiResponse acceptProjectInvitation(Long id) {
        ProjectJoin join = projectJoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectInvitation", "ID", id));
        // TODO : join 삭제 할지 말지?
        // TODO : 수락하는 사람이 마스터이냐 사용자이냐에 따라 구분해야함.
        join.setIsAccepted(Boolean.TRUE);
        join.setIsInvited(Boolean.TRUE);

        projectJoinRepository.save(join);

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
    public ApiResponse expelMember(Long projectId, String userId,User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        validateUserIsMaster(project, currentUser);
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

    // ---------------------------
    // -------- 검증 메소드 --------
    // ---------------------------
    // 마스터 검증
    public void validateUserIsMaster(Project project, User currentUser) {
        if(!project.getMaster().getId().equals(currentUser.getId()))
            throw new ResourceForbiddenException("권한이 없습니다.");
    }

    // 이미 ProjectJoin 있을 경우
    public Boolean isJoinAlreadyExist(Project project, User currentUser) {
        return projectJoinRepository.findByProjectAndUser(project, currentUser).isPresent();
    }

    // 프로젝트 멤버인지 아닌지
    public Boolean isUserMemberOfProject(Project project, User currentUser) {
        return projectMemberRepository.findByProjectAndUser(project, currentUser).isPresent();
    }

    // 프로젝트 멤버 검증
    public void validateUserIsMemberOfProject(Project project, User currentUser) {
        projectMemberRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("member of " + project.getName(), "userId", currentUser));
    }

}
