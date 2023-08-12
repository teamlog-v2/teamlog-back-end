package com.test.teamlog.domain.project.controller;

import com.test.teamlog.domain.post.dto.PostResponse;
import com.test.teamlog.domain.post.dto.PostResult;
import com.test.teamlog.domain.post.service.PostService;
import com.test.teamlog.domain.project.dto.*;
import com.test.teamlog.domain.project.dto.ProjectCreateRequest;
import com.test.teamlog.domain.project.dto.ProjectCreateResponse;
import com.test.teamlog.domain.project.dto.ProjectCreateResult;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.domain.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "프로젝트 관리")
public class ProjectApiController {
    private final ProjectService projectService;
    private final PostService postService;

    @Operation(summary = "프로젝트 생성")
    @PostMapping("/projects")
    public ResponseEntity<ProjectCreateResponse> create(@Valid @RequestBody ProjectCreateRequest request,
                                                        @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final ProjectCreateResult result = projectService.create(request.toInput(), currentUser.getUser());
        return new ResponseEntity<>(ProjectCreateResponse.from(result), HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 수정")
    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectUpdateResponse> update(@PathVariable("id") long id,
                                                             @Valid @RequestBody ProjectUpdateRequest request,
                                                             @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final ProjectUpdateResult result = projectService.update(id, request.toInput(), currentUser.getUser());
        return new ResponseEntity<>(ProjectUpdateResponse.from(result), HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 삭제")
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = projectService.delete(id, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "단일 프로젝트 조회")
    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectDTO.ProjectResponse> getProjectById(@PathVariable("id") long id,
                                                                     @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ProjectDTO.ProjectResponse response = projectService.getProject(id, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 마스터 위임")
    @PutMapping("/projects/{id}/master")
    public ResponseEntity<ApiResponse> delegateProjectMaster(@PathVariable("id") long id,
                                                             @RequestParam(value = "new-master", required = true) String newMasterId,
                                                             @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = projectService.delegateProjectMaster(id, newMasterId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 썸네일 변경")
    @PutMapping("/projects/{projectId}/thumbnail")
    public ResponseEntity<ApiResponse> updateUserProfileImage(@PathVariable("projectId") Long projectId,
                                                              @RequestPart(value = "thumbnail", required = true) MultipartFile image,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = projectService.updateProjectThumbnail(projectId, image, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 썸네일 삭제")
    @DeleteMapping("/projects/{projectId}/thumbnail")
    public ResponseEntity<ApiResponse> deleteUserProfileImage(@PathVariable("projectId") Long projectId,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = projectService.deleteProjectThumbnail(projectId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 팀 변경")
    @PutMapping("/projects/{id}/team")
    public ResponseEntity<ProjectDTO.ProjectResponse> setTeamInProject(@PathVariable("id") long id,
                                                                       @RequestBody ProjectDTO.ProjectRequest request,
                                                                       @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ProjectDTO.ProjectResponse response = projectService.setTeamInProject(id, request, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "연관 프로젝트 추천")
    @GetMapping("/recommended")
    public ResponseEntity<List<ProjectDTO.ProjectSimpleInfo>> getRecommendedProjects(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<ProjectDTO.ProjectSimpleInfo> response = null;
        if (currentUser != null) {
            response = projectService.getRecommendedProjects(currentUser.getUser());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 검색")
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO.ProjectListResponse>> searchProject(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<ProjectDTO.ProjectListResponse> response = projectService.searchProject(name, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저 프로젝트 리스트 조회")
    @GetMapping("/projects/accounts/{userId}")
    public ResponseEntity<List<ProjectDTO.ProjectListResponse>> getProjectsByUser(@PathVariable("userId") String userId,
                                                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<ProjectDTO.ProjectListResponse> response = projectService.getProjectsByUser(userId, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저 팔로잉 프로젝트 조회")
    @GetMapping("/accounts/{id}/following-projects")
    public ResponseEntity<List<ProjectDTO.ProjectListResponse>> getUserFollowingProjects(@PathVariable("id") String id,
                                                                                         @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<ProjectDTO.ProjectListResponse> response = projectService.getUserFollowingProjects(id, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "해시태그 추천")
    @GetMapping("/projects/{projectId}/recommended-hashtags")
    public ResponseEntity<List<String>> getRecommendedHashTags(@PathVariable("projectId") long projectId) {
        List<String> response = postService.getRecommendedHashTags(projectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "위치정보가 있는 프로젝트 게시물 조회")
    @GetMapping("/projects/{projectId}/posts/with-location")
    public ResponseEntity<List<PostResponse>> getLocationPosts(@PathVariable("projectId") long projectId,
                                                               @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<PostResult> resultList = postService.readAllWithLocation(projectId, currentUser.getUser());
        final List<PostResponse> response = resultList.stream().map(PostResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 내 게시물 전체 해시태그 조회")
    @GetMapping("/projects/{projectId}/hashtags")
    public ResponseEntity<List<String>> getHashTagsInProjectPosts(@PathVariable("projectId") long projectId) {
        List<String> response = postService.getHashTagsInProjectPosts(projectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}