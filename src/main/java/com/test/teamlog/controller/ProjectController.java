package com.test.teamlog.controller;

import com.test.teamlog.entity.Project;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.TaskDTO;
import com.test.teamlog.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
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
    public ResponseEntity<ProjectDTO.ProjectResponse> createProject(@RequestBody ProjectDTO.ProjectRequest request) {
        Long projectId = projectService.createProject(request);
        ProjectDTO.ProjectResponse response = projectService.getProject(projectId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "프로젝트 수정")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO.ProjectResponse> updateProject(@PathVariable("id") long id, @RequestBody ProjectDTO.ProjectRequest request) {
        projectService.updateProject(id, request);
        ProjectDTO.ProjectResponse projectResponse = projectService.getProject(id);
        return new ResponseEntity<>(projectResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id) {
        ApiResponse apiResponse = projectService.deleteProject(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

//    // 프로젝트 위임 : master가 요청했는지 .. 그런건 없다..
//    @PatchMapping("/{id}/master")
//
//    // 접근 권한 설정
//    @PatchMapping("/{id}")
//    // 프로젝트 초대 : 이미 멤버인 경우와 초대장을 보낸 경우에 대한 해결이 필요
//    @PostMapping("/{id}/invitation")
}