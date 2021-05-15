package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.ProjectJoinDTO;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Api(description = "프로젝트 초대 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProjectJoinController {
    private final ProjectService projectService;

    @ApiOperation(value = "프로젝트 멤버 초대(userId 필요) 및 신청")
    @PostMapping("/projects/{projectId}/joins")
    public ResponseEntity<ApiResponse> inviteUserForProject(@PathVariable("projectId") long projectId,
                                                            @RequestParam(value = "userId", required = false) String userId,
                                                            @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = null;
        // userId 있으면 초대, 없으면 신청
        if (userId != null) {
            apiResponse = projectService.inviteUserForProject(projectId, userId);
        } else {
            apiResponse = projectService.applyForProject(projectId, currentUser);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

//    @ApiOperation(value = "프로젝트 멤버 신청")
//    @PostMapping("/{id}/apply")
//    public ResponseEntity<ApiResponse> inviteUserForProject(@PathVariable("id") long projectId,
//                                                            @AuthenticationPrincipal User currentUser) {
//        ApiResponse apiResponse = projectService.applyForProject(projectId, currentUser);
//        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//    }

    @ApiOperation(value = "프로젝트 멤버 신청 삭제")
    @DeleteMapping("/project-joins/{joinId}")
    public ResponseEntity<ApiResponse> deleteProjectJoin(@PathVariable("joinId") Long joinId) {
        ApiResponse apiResponse = projectService.deleteProjectJoin(joinId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 멤버 신청자 목록 조회 ( 프로젝트 관리 )")
    @GetMapping("/projects/{id}/joins/apply")
    public ResponseEntity<List<ProjectJoinDTO.ProjectJoinForProject>> getProjectApplyListForProject(@PathVariable("id") Long id) {
        List<ProjectJoinDTO.ProjectJoinForProject> response = projectService.getProjectApplyListForProject(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 멤버로 초대한 사용자 목록 ( 프로젝트 관리 )")
    @GetMapping("/projects/{id}/joins/invitation")
    public ResponseEntity<List<ProjectJoinDTO.ProjectJoinForProject>> getProjectInvitationListForProject(@PathVariable("id") Long id) {
        List<ProjectJoinDTO.ProjectJoinForProject> response = projectService.getProjectInvitationListForProject(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "유저가 받은 프로젝트 초대 조회")
    @GetMapping("users/project-invitation")
    public ResponseEntity<List<ProjectJoinDTO.ProjectJoinForUser>> getProjectInvitationListForUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<ProjectJoinDTO.ProjectJoinForUser> response = projectService.getProjectInvitationListForUser(currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "유저가 가입 신청한 프로젝트 조회")
    @GetMapping("users/project-apply")
    public ResponseEntity<List<ProjectJoinDTO.ProjectJoinForUser>> getProjectApplyListForUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<ProjectJoinDTO.ProjectJoinForUser> response = projectService.getProjectApplyListForUser(currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}