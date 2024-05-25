package com.test.teamlog.domain.projectfollow.controller;

import com.test.teamlog.domain.projectfollow.dto.ProjectFollowerReadResponse;
import com.test.teamlog.domain.projectfollow.dto.ProjectFollowerReadResult;
import com.test.teamlog.domain.projectfollow.dto.ProjectFollowerReadAccountFollowedResponse;
import com.test.teamlog.domain.projectfollow.dto.ProjectFollowerReadAccountFollowedResult;
import com.test.teamlog.domain.projectfollow.service.ProjectFollowService;
import com.test.teamlog.global.security.AccountAdapter;
import com.test.teamlog.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "프로젝트 팔로우 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectFollowApiController {
    private final ProjectFollowService projectFollowService;

    @Operation(summary = "프로젝트 팔로우")
    @PostMapping("/projects/{projectId}/followers")
    public ResponseEntity<ApiResponse> followProject(@PathVariable("projectId") Long projectId,
                                                     @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        ApiResponse apiResponse = projectFollowService.followProject(projectId, currentAccount.getAccount());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 언팔로우")
    @DeleteMapping("/projects/{projectId}/followers")
    public ResponseEntity<ApiResponse> unfollowProject(@PathVariable("projectId") Long projectId,
                                                       @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        ApiResponse apiResponse = projectFollowService.unfollowProject(projectId, currentAccount.getAccount());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 팔로워 조회")
    @GetMapping("/projects/{projectId}/followers")
    public ResponseEntity<List<ProjectFollowerReadResponse>> readAll(@PathVariable("projectId") Long projectId) {
        final List<ProjectFollowerReadResult> resultList = projectFollowService.readAll(projectId);
        final List<ProjectFollowerReadResponse> responseList = resultList.stream().map(ProjectFollowerReadResponse::of).collect(Collectors.toList());

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(summary = "유저가 팔로우하는 프로젝트 조회")
    @GetMapping("/accounts/{accountId}/project-follow")
    public ResponseEntity<List<ProjectFollowerReadAccountFollowedResponse>> readAllAccountFollowed(@PathVariable("accountId") String accountId) {
        final List<ProjectFollowerReadAccountFollowedResult> resultList = projectFollowService.readAllByAccountIdentification(accountId);
        final List<ProjectFollowerReadAccountFollowedResponse> responseList = resultList.stream().map(ProjectFollowerReadAccountFollowedResponse::of).collect(Collectors.toList());

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
