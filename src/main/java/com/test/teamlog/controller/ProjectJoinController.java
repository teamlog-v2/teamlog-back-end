package com.test.teamlog.controller;

import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectJoinDTO;
import com.test.teamlog.domain.projectjoin.service.ProjectJoinService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProjectJoinController {
    private final ProjectJoinService projectJoinService;

    @Operation(summary = "프로젝트 멤버 초대(신청) 추가")
    @PostMapping("/projects/{projectId}/joins")
    public ResponseEntity<ApiResponse> inviteUserForProject(@PathVariable("projectId") long projectId,
                                                            @RequestParam(value = "userId", required = false) String userId,
                                                            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = null;
        // userId 있으면 초대, 없으면 신청
        if (userId != null) {
            apiResponse = projectJoinService.inviteUserForProject(projectId, userId);
        } else {
            apiResponse = projectJoinService.applyForProject(projectId, currentUser.getUser());
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 멤버 초대(신청) 삭제")
    @DeleteMapping("/project-joins/{joinId}")
    public ResponseEntity<ApiResponse> deleteProjectJoin(@PathVariable("joinId") Long joinId) {
        ApiResponse apiResponse = projectJoinService.deleteProjectJoin(joinId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 멤버 신청 목록 조회")
    @GetMapping("/projects/{id}/joins/apply")
    public ResponseEntity<List<ProjectJoinDTO.ProjectJoinForProject>> getProjectApplyListForProject(@PathVariable("id") Long id) {
        List<ProjectJoinDTO.ProjectJoinForProject> response = projectJoinService.getProjectApplyListForProject(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 멤버 초대 목록 조회")
    @GetMapping("/projects/{id}/joins/invitation")
    public ResponseEntity<List<ProjectJoinDTO.ProjectJoinForProject>> getProjectInvitationListForProject(@PathVariable("id") Long id) {
        List<ProjectJoinDTO.ProjectJoinForProject> response = projectJoinService.getProjectInvitationListForProject(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저가 가입 신청한 프로젝트 조회")
    @GetMapping("accounts/project-apply")
    public ResponseEntity<List<ProjectJoinDTO.ProjectJoinForUser>> getProjectApplyListForUser(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<ProjectJoinDTO.ProjectJoinForUser> response = projectJoinService.getProjectApplyListForUser(currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저가 받은 프로젝트 초대 조회")
    @GetMapping("accounts/project-invitation")
    public ResponseEntity<List<ProjectJoinDTO.ProjectJoinForUser>> getProjectInvitationListForUser(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<ProjectJoinDTO.ProjectJoinForUser> response = projectJoinService.getProjectInvitationListForUser(currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}