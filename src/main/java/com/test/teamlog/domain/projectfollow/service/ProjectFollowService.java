package com.test.teamlog.domain.projectfollow.service;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.project.service.query.ProjectQueryService;
import com.test.teamlog.domain.projectfollow.dto.ProjectFollowerReadResult;
import com.test.teamlog.domain.projectfollow.dto.ProjectFollowerReadAccountFollowedResult;
import com.test.teamlog.domain.projectfollow.entity.ProjectFollower;
import com.test.teamlog.domain.projectfollow.repository.ProjectFollowerRepository;
import com.test.teamlog.global.exception.ResourceAlreadyExistsException;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import com.test.teamlog.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectFollowService {
    private final ProjectFollowerRepository projectFollowerRepository;

    private final AccountQueryService accountQueryService;
    private final ProjectQueryService projectQueryService;

    // 유저가 팔로우하는 프로젝트 목록 조회
    public List<ProjectFollowerReadAccountFollowedResult> readAllByaccountIdentification(String accountIdentification) {
        Account account = accountQueryService.findByIdentification(accountIdentification)
                .orElseThrow(() -> new ResourceNotFoundException("ACCOUNT", "ID", accountIdentification));
        List<ProjectFollower> projectFollowerList = projectFollowerRepository.findAllByAccount(account);

        return projectFollowerList.stream().map(ProjectFollowerReadAccountFollowedResult::of).collect(Collectors.toList());
    }

    // 해당 프로젝트를 팔로우하는 사용자 목록 조회
    public List<ProjectFollowerReadResult> readAll(Long projectId) {
        final Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));

        List<ProjectFollower> projectFollowerList = projectFollowerRepository.findAllByProject(project);

        return projectFollowerList.stream().map(ProjectFollowerReadResult::of).collect(Collectors.toList());
    }

    // 프로젝트 팔로우
    @Transactional
    public ApiResponse followProject(Long projectId, Account currentAccount) {
        final Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));

        projectFollowerRepository.findByProjectAndAccount(project, currentAccount)
                .ifPresent(projectFollower -> {
                    throw new ResourceAlreadyExistsException("이미 해당 프로젝트를 팔로우 하고 있습니다.");
                });

        projectFollowerRepository.save(ProjectFollower.create(project, currentAccount));

        return new ApiResponse(Boolean.TRUE, "프로젝트 팔로우 성공");
    }

    // 프로젝트 언팔로우
    @Transactional
    public ApiResponse unfollowProject(Long projectId, Account currentAccount) {
        final Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));

        ProjectFollower projectFollower = projectFollowerRepository.findByProjectAndAccount(project, currentAccount)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectFollwer", "accountId", currentAccount.getIdentification()));

        projectFollowerRepository.delete(projectFollower);
        return new ApiResponse(Boolean.TRUE, "프로젝트 언팔로우 성공");
    }
}
