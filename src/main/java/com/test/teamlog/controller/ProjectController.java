package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.service.ProjectService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*" )
public class ProjectController {
    private final ProjectService projectService;

    @ApiOperation(value = "단일 프로젝트 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO.ProjectResponse> getProjectById(@PathVariable("id") long id) {
        ProjectDTO.ProjectResponse response = projectService.getProject(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "유저 프로젝트 리스트 조회")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProjectDTO.ProjectListResponse>> getProjectsByUser(@PathVariable("userId") String userId) {
        List<ProjectDTO.ProjectListResponse> response = projectService.getProjectsByUser(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 생성")
    @PostMapping
    public ResponseEntity<ApiResponse> createProject(@RequestBody ProjectDTO.ProjectRequest request,
                                                     @ApiIgnore  @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = projectService.createProject(request, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "프로젝트 수정")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProject(@PathVariable("id") long id,
                                                     @RequestBody ProjectDTO.ProjectRequest request,
                                                     @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = projectService.updateProject(id, request, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProject(@PathVariable("id") Long id,
                                                     @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = projectService.deleteProject(id, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 멤버 조회")
    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserDTO.UserSimpleInfo>> getProjectMemberList(@PathVariable("id") Long id) {
        List<UserDTO.UserSimpleInfo> response = projectService.getProjectMemberList(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .body(response);
    }

//    // 프로젝트 위임 : master가 요청했는지 .. 그런건 없다..
//    @PatchMapping("/{id}/master")
//
//    // 접근 권한 설정
//    @PatchMapping("/{id}")
//    // 프로젝트 초대 : 이미 멤버인 경우와 초대장을 보낸 경우에 대한 해결이 필요
//    @PostMapping("/{id}/invitation")
}