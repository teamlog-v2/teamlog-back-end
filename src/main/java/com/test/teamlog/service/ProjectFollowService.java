package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.repository.PostRepository;
import com.test.teamlog.repository.ProjectFollowerRepository;
import com.test.teamlog.repository.ProjectRepository;
import com.test.teamlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectFollowService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ProjectRepository projectRepository;
    private final ProjectFollowerRepository projectFollowerRepository;

    // 유저가 팔로우하는 프로젝트 목록 조회
    public List<ProjectDTO.ProjectListResponse> getProjectListByProjectFollower(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
        List<ProjectFollower> projectFollowers = projectFollowerRepository.findAllByUser(user);

        List<ProjectDTO.ProjectListResponse> projectList = new ArrayList<>();
        for (ProjectFollower follower : projectFollowers) {
            long postcount = postRepository.getPostsCount(follower.getProject());
            ProjectDTO.ProjectListResponse temp = ProjectDTO.ProjectListResponse.builder()
                    .id(follower.getProject().getId())
                    .name(follower.getProject().getName())
                    .postCount(postcount)
                    .updateTime(follower.getProject().getUpdateTime())
                    .thumbnail(null)
                    .build();
            projectList.add(temp);
        }

        return projectList;
    }

    // 해당 프로젝트를 팔로우하는 사용자 목록 조회
    public List<UserDTO.UserSimpleInfo> getProjectFollowerList(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));

        List<ProjectFollower> projectFollowers = projectFollowerRepository.findAllByProject(project);

        List<UserDTO.UserSimpleInfo> userList = new ArrayList<>();
        for (ProjectFollower follower : projectFollowers) {
            userList.add(new UserDTO.UserSimpleInfo(follower.getUser()));
        }
        return userList;
    }

    // 프로젝트 팔로우
    @Transactional
    public ApiResponse followProject(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));

        ProjectFollower newFollow = ProjectFollower.builder()
                .project(project)
                .user(currentUser)
                .build();

        projectFollowerRepository.save(newFollow);
        return new ApiResponse(Boolean.TRUE, "프로젝트 팔로우 성공");
    }

    // 프로젝트 언팔로우
    @Transactional
    public ApiResponse unfollowProject(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));
        ProjectFollower projectFollower = projectFollowerRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectFollwer", "UserId", currentUser.getId()));
        projectFollowerRepository.delete(projectFollower);
        return new ApiResponse(Boolean.TRUE, "프로젝트 언팔로우 성공");
    }

}
