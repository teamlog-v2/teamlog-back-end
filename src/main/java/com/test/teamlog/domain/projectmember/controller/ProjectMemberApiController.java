package com.test.teamlog.domain.projectmember.controller;

import com.test.teamlog.domain.projectmember.dto.ProjectMemberReadResponse;
import com.test.teamlog.domain.projectmember.dto.ProjectMemberReadResult;
import com.test.teamlog.domain.projectmember.service.ProjectMemberService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.security.UserAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "프로젝트 멤버 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProjectMemberApiController {
    private final ProjectMemberService projectMemberService;

    @Operation(summary = "프로젝트 초대 수락")
    @PostMapping("/projects/{projectId}/members")
    public ResponseEntity<ApiResponse> create(@PathVariable("projectId") Long projectId,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = projectMemberService.create(projectId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 멤버 삭제")
    @DeleteMapping("/projects/{projectId}/members")
    public ResponseEntity<ApiResponse> delete(@PathVariable("projectId") long projectId,
                                              @RequestParam(value = "userId", required = false) String userId,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse;
        // userId 없으면 탈퇴 있으면 추방
        if (userId == null) {
            apiResponse = projectMemberService.leaveProject(projectId, currentUser.getUser());
        } else {
            apiResponse = projectMemberService.expelMember(projectId, userId, currentUser.getUser());
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 멤버 조회")
    @GetMapping("/projects/{projectId}/members")
    public ResponseEntity<List<ProjectMemberReadResponse>> readAll(@PathVariable("projectId") Long projectId) {
        final List<ProjectMemberReadResult> resultList = projectMemberService.readAll(projectId);
        final List<ProjectMemberReadResponse> responseList = resultList.stream().map(ProjectMemberReadResponse::of).collect(Collectors.toList());

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    // FIXME: 개선 포인트. 해당 API는 삭제 예정이며 우선 dto는 ProjectMemberRead- 로 사용 (형태가 동일하다)
//    @Deprecated
//    @Operation(summary = "프로젝트 멤버가 아닌 유저 조회")
//    @GetMapping("/projects/{projectId}/not-members")
//    public ResponseEntity<List<UserRequest.UserSimpleInfo>> readAllNotInProjectMember(@PathVariable("projectId") Long projectId) {
//        List<UserRequest.UserSimpleInfo> response = projectMemberService.readAllNotInProjectMember(projectId);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}