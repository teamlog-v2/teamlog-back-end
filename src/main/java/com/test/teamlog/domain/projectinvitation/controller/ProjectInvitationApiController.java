package com.test.teamlog.domain.projectinvitation.controller;

import com.test.teamlog.domain.projectinvitation.dto.*;
import com.test.teamlog.domain.projectinvitation.service.ProjectInvitationService;
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
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {

        final ApiResponse apiResponse = projectInvitationService.create(request.toInput(currentUser.getUser().getIdx()));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ApiResponse> accept(
            @RequestBody ProjectInvitationAcceptRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {

        final ApiResponse apiResponse = projectInvitationService.accept(request.toInput(currentUser.getUser().getIdx()));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> delete(
            @RequestBody ProjectInvitationDeleteRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {

        final ApiResponse apiResponse = projectInvitationService.delete(request.toInput(currentUser.getUser().getIdx()));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/invitees")
    public ResponseEntity<List<ProjectInvitationReadInviteeResponse>> readAllInvitees(
            @RequestParam Long projectIdx,
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser
    ) {
        final List<ProjectInvitationReadInviteeResult> resultList = projectInvitationService.readAllInvitee(currentUser.getUser().getIdx(), projectIdx);
        final List<ProjectInvitationReadInviteeResponse> responseList = resultList.stream().map(ProjectInvitationReadInviteeResponse::from).toList();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ProjectInvitationReadPendingResponse>> readAllPending(
            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser
    ) {
        final List<ProjectInvitationReadPendingResult> resultList = projectInvitationService.readAllPending(currentUser.getUser().getIdx());
        final List<ProjectInvitationReadPendingResponse> responseList = resultList.stream().map(ProjectInvitationReadPendingResponse::from).toList();

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
