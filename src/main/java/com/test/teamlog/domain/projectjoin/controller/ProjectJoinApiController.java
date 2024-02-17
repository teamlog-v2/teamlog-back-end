package com.test.teamlog.domain.projectjoin.controller;

import com.test.teamlog.domain.projectjoin.dto.ProjectJoinApplyRequest;
import com.test.teamlog.domain.projectjoin.dto.ProjectJoinForProject;
import com.test.teamlog.domain.projectjoin.dto.ProjectJoinForAccount;
import com.test.teamlog.domain.projectjoin.dto.ProjectJoinInviteRequest;
import com.test.teamlog.domain.projectjoin.service.ProjectJoinService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.security.AccountAdapter;
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
public class ProjectJoinApiController {
    private final ProjectJoinService projectJoinService;

    @Operation(summary = "프로젝트 멤버 초대(신청) 추가")
    @PostMapping("/projects/{projectId}/joins")
    public ResponseEntity<ApiResponse> inviteAccountForProject(@PathVariable("projectId") long projectId,
                                                               @RequestParam(value = "accountId", required = false) String accountId,
                                                               @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        ApiResponse apiResponse = null;
        // accountId 있으면 초대, 없으면 신청
        if (accountId != null) {
            apiResponse = projectJoinService.inviteAccountForProject(projectId, accountId);
        } else {
            apiResponse = projectJoinService.applyForProjectV1(projectId, currentAccount.getAccount());
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 멤버 초대")
    @PostMapping("/project-joins/invite")
    public ResponseEntity<ApiResponse> invite(@RequestBody ProjectJoinInviteRequest request,
                                              @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        ApiResponse apiResponse = projectJoinService.invite(request.toInput(), currentAccount.getAccount());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 멤버 신청")
    @PostMapping("/project-joins/apply")
    public ResponseEntity<ApiResponse> apply(@RequestBody ProjectJoinApplyRequest request,
                                             @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        ApiResponse apiResponse = projectJoinService.apply(request.toInput(), currentAccount.getAccount());

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
    public ResponseEntity<List<ProjectJoinForProject>> getProjectApplyListForProject(@PathVariable("id") Long id) {
        List<ProjectJoinForProject> response = projectJoinService.getProjectApplyListForProject(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "유저가 가입 신청한 프로젝트 조회")
    @GetMapping("accounts/project-apply")
    public ResponseEntity<List<ProjectJoinForAccount>> getProjectApplyListForAccount(@Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        List<ProjectJoinForAccount> response = projectJoinService.getProjectApplyListForAccount(currentAccount.getAccount());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 멤버 초대 목록 조회")
    @GetMapping("/projects/{id}/joins/invitation")
    public ResponseEntity<List<ProjectJoinForProject>> getProjectInvitationListForProject(@PathVariable("id") Long id) {
        List<ProjectJoinForProject> response = projectJoinService.getProjectInvitationListForProject(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저가 받은 프로젝트 초대 조회")
    @GetMapping("accounts/project-invitation")
    public ResponseEntity<List<ProjectJoinForAccount>> getProjectInvitationListForAccount(@Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        List<ProjectJoinForAccount> response = projectJoinService.getProjectInvitationListForAccount(currentAccount.getAccount());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}