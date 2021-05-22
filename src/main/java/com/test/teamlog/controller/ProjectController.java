package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProjectController {
    private final ProjectService projectService;

    @ApiOperation(value = "유저가 팔로잉 중인 프로젝트")
    @GetMapping("/users/{id}/following-projects")
    public ResponseEntity<List<ProjectDTO.ProjectListResponse>> getUserFollowingProjects(@PathVariable("id") String id,
                                                                                         @ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<ProjectDTO.ProjectListResponse> response = projectService.getUserFollowingProjects(id, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 검색")
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO.ProjectListResponse>> searchProject(@RequestParam(value = "name", required = false, defaultValue = "") String name) {
        List<ProjectDTO.ProjectListResponse> response = projectService.searchProject(name);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "단일 프로젝트 조회")
    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectDTO.ProjectResponse> getProjectById(@PathVariable("id") long id,
                                                                     @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ProjectDTO.ProjectResponse response = projectService.getProject(id, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "유저 프로젝트 리스트 조회")
    @GetMapping("/projects/user/{userId}")
    public ResponseEntity<List<ProjectDTO.ProjectListResponse>> getProjectsByUser(@PathVariable("userId") String
                                                                                          userId) {
        List<ProjectDTO.ProjectListResponse> response = projectService.getProjectsByUser(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 생성")
    @PostMapping("/projects")
    public ResponseEntity<ProjectDTO.ProjectResponse> createProject(@RequestBody ProjectDTO.ProjectRequest request,
                                                                    @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ProjectDTO.ProjectResponse response = projectService.createProject(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "프로젝트 수정")
    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectDTO.ProjectResponse> updateProject(@PathVariable("id") long id,
                                                                    @RequestBody ProjectDTO.ProjectRequest request,
                                                                    @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ProjectDTO.ProjectResponse response = projectService.updateProject(id, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 마스터 위임")
    @PutMapping("/projects/{id}/master")
    public ResponseEntity<ApiResponse> delegateProjectMaster(@PathVariable("id") long id,
                                                             @RequestParam(value = "new-master", required = true) String newMasterId,
                                                             @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = projectService.delegateProjectMaster(id, newMasterId, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @ApiOperation(value = "프로젝트 삭제")
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<ApiResponse> deleteProject(@PathVariable("id") Long id,
                                                     @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = projectService.deleteProject(id, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}