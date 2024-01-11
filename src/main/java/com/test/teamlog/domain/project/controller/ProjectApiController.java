package com.test.teamlog.domain.project.controller;

import com.test.teamlog.domain.project.dto.*;
import com.test.teamlog.domain.project.service.ProjectService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.security.UserAdapter;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "프로젝트 관리")
public class ProjectApiController {
    private final ProjectService projectService;

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
    public ResponseEntity<ProjectReadResponse> readOne(@PathVariable("id") long id,
                                                       @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final ProjectReadResult result = projectService.readOne(id, currentUser.getUser());
        return new ResponseEntity<>(ProjectReadResponse.from(result), HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 마스터 위임")
    @PutMapping("/projects/{id}/master")
    public ResponseEntity<ApiResponse> delegateMaster(@PathVariable("id") long id,
                                                      @RequestParam(value = "new-master", required = true) String newMasterId,
                                                      @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = projectService.delegateMaster(id, newMasterId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 썸네일 변경")
    @PutMapping("/projects/{projectId}/thumbnail")
    public ResponseEntity<ApiResponse> updateThumbnail(@PathVariable("projectId") Long projectId,
                                                       @RequestPart(value = "thumbnail") MultipartFile image,
                                                       @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        try {
            ApiResponse apiResponse = projectService.updateThumbnail(projectId, image, currentUser.getUser());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "프로젝트 썸네일 삭제")
    @DeleteMapping("/projects/{projectId}/thumbnail")
    public ResponseEntity<ApiResponse> deleteThumbnail(@PathVariable("projectId") Long projectId,
                                                       @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = projectService.deleteThumbnail(projectId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 검색")
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectSearchResponse>> search(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final List<ProjectSearchResult> resultList = projectService.search(name, currentUser.getUser());
        final List<ProjectSearchResponse> responseList = resultList.stream().map(ProjectSearchResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(summary = "유저 프로젝트 리스트 조회")
    @GetMapping("/projects/accounts/{userId}")
    public ResponseEntity<List<ProjectReadByUserResponse>> readAllByUser(@PathVariable("userId") String userId,
                                                                         @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final List<ProjectReadByUserResult> resultList = projectService.readAllByUser(userId, currentUser.getUser());
        final List<ProjectReadByUserResponse> responseList = resultList.stream().map(ProjectReadByUserResponse::from).collect(Collectors.toList());
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(summary = "유저 팔로잉 프로젝트 조회")
    @GetMapping("/accounts/{id}/following-projects")
    public ResponseEntity<List<ProjectReadUserFollowingResponse>> readAllUserFollowing(@PathVariable("id") String identification,
                                                                                       @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final List<ProjectReadUserFollowingResult> resultList = projectService.readAllUserFollowing(identification, currentUser.getUser());
        List<ProjectReadUserFollowingResponse> response = resultList.stream().map(ProjectReadUserFollowingResponse::from).collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 내 게시물 전체 해시태그 조회")
    @GetMapping("/projects/{projectId}/hashtags")
    public ResponseEntity<List<String>> getHashTagsInProjectPosts(@PathVariable("projectId") long projectId) {
        List<String> hashTagList = projectService.readHashTagsInProjectPosts(projectId);
        return new ResponseEntity<>(hashTagList, HttpStatus.OK);
    }
}