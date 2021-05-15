package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectJoinDTO;
import com.test.teamlog.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(description = "프로젝트 멤버 관리 ( 프로젝트 초대 수락은 여기있음 )")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProjectMemberController {
    private final ProjectService projectService;

    @ApiOperation(value = "프로젝트 초대 및 신청을 수락")
    @PostMapping("/project-joins/{joinId}")
    public ResponseEntity<ApiResponse> acceptProjectInvitation(@PathVariable("joinId") Long joinId) {
        ApiResponse apiResponse = projectService.acceptProjectInvitation(joinId);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "프로젝트 멤버 탈퇴/ 추방(userId 필요) : 임시")
    @DeleteMapping("/projects/{projectId}/members")
    public ResponseEntity<ApiResponse> leaveProject(@PathVariable("projectId") long projectId,
                                                    @RequestParam(value = "userId", required = false) String userId,
                                                    @ApiIgnore  @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = null;
        // userId 없으면 탈퇴 있으면 추방
        if(userId == null) {
            apiResponse = projectService.leaveProject(projectId, currentUser);
        } else {
            apiResponse = projectService.expelMember(projectId, userId, currentUser);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 멤버 삭제 By ProjectMember key")
    @DeleteMapping("/project-members/{id}")
    public ResponseEntity<ApiResponse> leaveProject(@PathVariable("id") long id) {
        ApiResponse apiResponse = projectService.deleteProjectMemeber(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}