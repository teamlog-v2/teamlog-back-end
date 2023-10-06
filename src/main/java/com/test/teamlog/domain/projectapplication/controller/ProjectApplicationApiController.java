package com.test.teamlog.domain.projectapplication.controller;

import com.test.teamlog.domain.projectapplication.dto.*;
import com.test.teamlog.domain.projectapplication.service.ProjectApplicationService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.security.UserAdapter;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "프로젝트 지원 관리")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/project-applications")
public class ProjectApplicationApiController {
    private final ProjectApplicationService projectApplicationService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @RequestBody ProjectApplicationCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {

        final ApiResponse apiResponse = projectApplicationService.create(request.toInput(currentUser.getUser().getIdx()));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse> accept(
            @RequestBody ProjectApplicationAcceptRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {

        final ApiResponse apiResponse = projectApplicationService.accept(request.toInput(currentUser.getUser().getIdx()));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/reject/{idx}")
    public ResponseEntity<ApiResponse> reject(
            @PathVariable Long idx,
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {

        final ApiResponse apiResponse = projectApplicationService.reject(idx, currentUser.getUser().getIdx());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{idx}")
    public ResponseEntity<ApiResponse> cancel(
            @PathVariable Long idx,
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {

        final ApiResponse apiResponse = projectApplicationService.cancel(idx, currentUser.getUser().getIdx());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    /**
     * 특정 프로젝트 지원자 목록 조회
     *
     * @param projectIdx
     * @param currentUser
     * @return
     */
    @GetMapping("/applicants")
    public ResponseEntity<List<ProjectApplicationReadApplicantsResponse>> readAllApplicants(
            @RequestParam Long projectIdx,
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser
    ) {
        final List<ProjectApplicationReadApplicantsResult> resultList = projectApplicationService.readAllApplicants(projectIdx, currentUser.getUser().getIdx());
        final List<ProjectApplicationReadApplicantsResponse> responseList = resultList.stream().map(ProjectApplicationReadApplicantsResponse::from).toList();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    /**
     * 본인이 지원한 프로젝트 목록 조회
     *
     * @param currentUser
     * @return
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ProjectApplicationReadPendingResponse>> readAllPending(
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser
    ) {
        final List<ProjectApplicationReadPendingResult> resultList = projectApplicationService.readAllPending(currentUser.getUser().getIdx());
        final List<ProjectApplicationReadPendingResponse> responseList = resultList.stream().map(ProjectApplicationReadPendingResponse::from).toList();

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
