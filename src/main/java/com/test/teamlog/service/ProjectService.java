package com.test.teamlog.service;

import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.ProjectMember;
import com.test.teamlog.entity.User;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.repository.PostRepository;
import com.test.teamlog.repository.ProjectMemberRepository;
import com.test.teamlog.repository.ProjectRepository;
import com.test.teamlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PostRepository postRepository;

    // 단일 프로젝트 조회
    public ProjectDTO.ProjectResponse getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        ProjectDTO.ProjectResponse projectResponse = ProjectDTO.ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .introduction(project.getIntroduction())
                .accessModifier(project.getAccessModifier())
                .masterId(project.getMaster().getId())
                .memberCount(project.getProjectMembers().size())
                .followerCount(project.getProjectFollowers().size())
                .createTime(project.getCreateTime())
                .build();
        return projectResponse;
    }

    // 사용자 프로젝트 리스트 조회
    public List<ProjectDTO.ProjectListResponse> getProjectsByUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("USER", "id", id));

        List<ProjectMember> projectList = projectMemberRepository.findByUser(user);

        List<ProjectDTO.ProjectListResponse> projects = new ArrayList<>();
        for (ProjectMember project : projectList) {
            int postcount = postRepository.getPostCount(project.getProject());
            ProjectDTO.ProjectListResponse item = ProjectDTO.ProjectListResponse.builder()
                    .id(project.getProject().getId())
                    .name(project.getProject().getName())
                    .postCount(postcount)
                    .updateTime(project.getProject().getUpdateTime())
                    .build();
            projects.add(item);
        }
        return projects;
    }

    // 프로젝트 생성
    @Transactional
    public ApiResponse createProject(ProjectDTO.ProjectRequest request) {
        User master = userRepository.findById(request.getMasterId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getMasterId()));

        Project project = Project.builder()
                .name(request.getName())
                .introduction(request.getIntroduction())
                .accessModifier(request.getAccessModifier())
                .master(master)
                .build();
        projectRepository.save(project);

        ProjectMember member = ProjectMember.builder()
                .user(master)
                .project(project)
                .build();
        projectMemberRepository.save(member);

        return new ApiResponse(Boolean.TRUE, "프로젝트 생성 성공");
    }

    // 프로젝트 수정
    @Transactional
    public ApiResponse updateProject(Long id, ProjectDTO.ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        project.setName(request.getName());
        project.setIntroduction(request.getIntroduction());
        project.setAccessModifier(request.getAccessModifier());

        if (request.getMasterId() != null) {
            User master = userRepository.findById(request.getMasterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getMasterId()));
            project.setMaster(master);
        }
        projectRepository.save(project);

        return new ApiResponse(Boolean.TRUE, "프로젝트 수정 성공");
    }

    // 프로젝트 삭제
    @Transactional
    public ApiResponse deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));

        projectRepository.delete(project);
        return new ApiResponse(Boolean.TRUE, "프로젝트 삭제 성공");
    }

    // 프로젝트 멤버 조회
    public List<UserDTO.UserSimpleInfo> getProjectMemberList(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        List<ProjectMember> members = projectMemberRepository.findByProject(project);

        List<UserDTO.UserSimpleInfo> memberList = new ArrayList<>();
        for(ProjectMember member : members) {
            memberList.add(new UserDTO.UserSimpleInfo(member.getUser()));
        }

        return memberList;
    }
//    // 프로젝트 초대
//    @Transactional
//    public ApiResponse inviteProject(Long id) {
//
//    }
}
