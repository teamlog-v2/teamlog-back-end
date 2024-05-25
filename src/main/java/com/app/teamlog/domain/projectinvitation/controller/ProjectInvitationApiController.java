package com.app.teamlog.domain.projectinvitation.controller;

import com.app.teamlog.domain.projectinvitation.dto.*;
import com.app.teamlog.domain.projectinvitation.service.ProjectInvitationService;
import com.app.teamlog.global.dto.ApiResponse;
import com.app.teamlog.global.security.AccountAdapter;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "프로젝트 초대 관리")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/project-invitations")
public class ProjectInvitationApiController {
    private final ProjectInvitationService projectInvitationService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @RequestBody ProjectInvitationCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {

        final ApiResponse apiResponse = projectInvitationService.create(request.toInput(currentAccount.getAccount().getIdx()));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse> accept(
            @RequestBody ProjectInvitationAcceptRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {

        final ApiResponse apiResponse = projectInvitationService.accept(request.toInput(currentAccount.getAccount().getIdx()));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/reject")
    public ResponseEntity<ApiResponse> reject(
            @RequestBody ProjectInvitationRejectRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {

        final ApiResponse apiResponse = projectInvitationService.reject(request.toInput(currentAccount.getAccount().getIdx()));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<ApiResponse> cancel(
            @RequestBody ProjectInvitationCancelRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {

        final ApiResponse apiResponse = projectInvitationService.cancel(request.toInput(currentAccount.getAccount().getIdx()));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    /**
     * 특정 프로젝트에 초대 받은 유저 목록 조회
     * @param projectIdx
     * @param currentAccount
     * @return
     */
    @GetMapping("/invitees")
    public ResponseEntity<List<ProjectInvitationReadInviteeResponse>> readAllInvitees(
            @RequestParam Long projectIdx,
            @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount
    ) {
        final List<ProjectInvitationReadInviteeResult> resultList = projectInvitationService.readAllInvitee(projectIdx, currentAccount.getAccount().getIdx());
        final List<ProjectInvitationReadInviteeResponse> responseList = resultList.stream().map(ProjectInvitationReadInviteeResponse::from).toList();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    /**
     * 본인이 아직 수락하지 않은 프로젝트 초대 목록 조회
     * @param currentAccount
     * @return
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ProjectInvitationReadPendingResponse>> readAllPending(
            @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount
    ) {
        final List<ProjectInvitationReadPendingResult> resultList = projectInvitationService.readAllPending(currentAccount.getAccount().getIdx());
        final List<ProjectInvitationReadPendingResponse> responseList = resultList.stream().map(ProjectInvitationReadPendingResponse::from).toList();

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
