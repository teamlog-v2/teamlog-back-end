package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.service.ProjectFollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(description = "프로젝트 팔로우 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectFollowController {
    private final ProjectFollowService projectFollowService;

    @ApiOperation(value = "유저가 팔로우하는 프로젝트 목록 조회")
    @GetMapping("/users/{userId}/project-follow")
    public ResponseEntity<List<ProjectDTO.ProjectListResponse>> getFollowingProjectListByUser(@PathVariable("userId") String userId) {
        List<ProjectDTO.ProjectListResponse> response = projectFollowService.getProjectListByProjectFollower(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트를 팔로우하는 사용자 목록 조회")
    @GetMapping("/projects/{projectId}/followers")
    public ResponseEntity<List<UserDTO.UserSimpleInfo>> getProjectFollowerList(@PathVariable("projectId") Long projectId) {
        List<UserDTO.UserSimpleInfo> response = projectFollowService.getProjectFollowerList(projectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 팔로우")
    @PostMapping("/projects/{projectId}/followers")
    public ResponseEntity<ApiResponse> followProject(@PathVariable("projectId") Long projectId,
                                                     @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = projectFollowService.followProject(projectId, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "프로젝트 언팔로우")
    @DeleteMapping("/projects/{projectId}/followers")
    public ResponseEntity<ApiResponse> unfollowProject(@PathVariable("projectId") Long projectId,
                                                       @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = projectFollowService.unfollowProject(projectId, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
