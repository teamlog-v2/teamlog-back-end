package com.test.teamlog.controller;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "프로젝트 멤버 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;

    @Operation(summary = "프로젝트 초대(신청) 수락")
    @PostMapping("/project-joins/{joinId}")
    public ResponseEntity<ApiResponse> acceptProjectInvitation(@PathVariable("joinId") Long joinId) {
        ApiResponse apiResponse = projectMemberService.acceptProjectInvitation(joinId);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 초대 수락")
    @PostMapping("/projects/{projectId}/members")
    public ResponseEntity<ApiResponse> createProjectMember(@PathVariable("projectId") Long projectId,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = projectMemberService.createProjectMember(projectId, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 멤버 삭제")
    @DeleteMapping("/projects/{projectId}/members")
    public ResponseEntity<ApiResponse> leaveProject(@PathVariable("projectId") long projectId,
                                                    @RequestParam(value = "userId", required = false) String userId,
                                                    @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = null;
        // userId 없으면 탈퇴 있으면 추방
        if (userId == null) {
            apiResponse = projectMemberService.leaveProject(projectId, currentUser);
        } else {
            apiResponse = projectMemberService.expelMember(projectId, userId, currentUser);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 멤버 조회")
    @GetMapping("/projects/{projectId}/members")
    public ResponseEntity<List<UserRequest.UserSimpleInfo>> getProjectMemberList(@PathVariable("projectId") Long projectId) {
        List<UserRequest.UserSimpleInfo> response = projectMemberService.getProjectMemberList(projectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 멤버가 아닌 유저 조회")
    @GetMapping("/projects/{projectId}/not-members")
    public ResponseEntity<List<UserRequest.UserSimpleInfo>> get(@PathVariable("projectId") Long projectId) {
        List<UserRequest.UserSimpleInfo> response = projectMemberService.getUsersNotInProjectMember(projectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}