package com.test.teamlog.controller;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.service.ProjectFollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "프로젝트 팔로우 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectFollowController {
    private final ProjectFollowService projectFollowService;

    @Operation(summary = "프로젝트 팔로우")
    @PostMapping("/projects/{projectId}/followers")
    public ResponseEntity<ApiResponse> followProject(@PathVariable("projectId") Long projectId,
                                                     @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = projectFollowService.followProject(projectId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 언팔로우")
    @DeleteMapping("/projects/{projectId}/followers")
    public ResponseEntity<ApiResponse> unfollowProject(@PathVariable("projectId") Long projectId,
                                                       @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = projectFollowService.unfollowProject(projectId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 팔로워 조회")
    @GetMapping("/projects/{projectId}/followers")
    public ResponseEntity<List<UserRequest.UserSimpleInfo>> getProjectFollowerList(@PathVariable("projectId") Long projectId) {
        List<UserRequest.UserSimpleInfo> response = projectFollowService.getProjectFollowerList(projectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저가 팔로우하는 프로젝트 조회")
    @GetMapping("/accounts/{userId}/project-follow")
    public ResponseEntity<List<ProjectDTO.ProjectListResponse>> getFollowingProjectListByUser(@PathVariable("userId") String userId) {
        List<ProjectDTO.ProjectListResponse> response = projectFollowService.getProjectListByProjectFollower(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
