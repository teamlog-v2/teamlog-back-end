package com.test.teamlog.domain.projectfollow.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.repository.AccountRepository;
import com.test.teamlog.domain.projectfollow.dto.ProjectFollowerReadResult;
import com.test.teamlog.domain.projectfollow.dto.ProjectFollowerReadUserFollowedResult;
import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.ProjectFollower;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.domain.projectfollow.repository.ProjectFollowerRepository;
import com.test.teamlog.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectFollowService {
    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final ProjectFollowerRepository projectFollowerRepository;

    // 유저가 팔로우하는 프로젝트 목록 조회
    public List<ProjectFollowerReadUserFollowedResult> readAllByUserIdentification(String userIdentification) {
        User user = accountRepository.findByIdentification(userIdentification)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", userIdentification));
        List<ProjectFollower> projectFollowerList = projectFollowerRepository.findAllByUser(user);

        return projectFollowerList.stream().map(ProjectFollowerReadUserFollowedResult::of).collect(Collectors.toList());
    }

    // 해당 프로젝트를 팔로우하는 사용자 목록 조회
    public List<ProjectFollowerReadResult> readAll(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));

        List<ProjectFollower> projectFollowerList = projectFollowerRepository.findAllByProject(project);

        return projectFollowerList.stream().map(ProjectFollowerReadResult::of).collect(Collectors.toList());
    }

    // 프로젝트 팔로우
    @Transactional
    public ApiResponse followProject(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));

        projectFollowerRepository.findByProjectAndUser(project, currentUser)
                .ifPresent(projectFollower -> {
                    throw new ResourceAlreadyExistsException("이미 해당 프로젝트를 팔로우 하고 있습니다.");
                });

        projectFollowerRepository.save(ProjectFollower.create(project, currentUser));

        return new ApiResponse(Boolean.TRUE, "프로젝트 팔로우 성공");
    }

    // 프로젝트 언팔로우
    @Transactional
    public ApiResponse unfollowProject(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));
        ProjectFollower projectFollower = projectFollowerRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectFollwer", "UserId", currentUser.getIdentification()));

        projectFollowerRepository.delete(projectFollower);
        return new ApiResponse(Boolean.TRUE, "프로젝트 언팔로우 성공");
    }
}
