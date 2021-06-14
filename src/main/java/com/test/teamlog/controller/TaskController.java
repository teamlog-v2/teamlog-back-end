package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TaskDTO;
import com.test.teamlog.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api(tags = "태스크 관리")
public class TaskController {
    private final TaskService taskService;

    @ApiOperation(value = "태스크 생성")
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskDTO.TaskResponse> createTask(@PathVariable("projectId") Long projectId,
                                                           @Valid @RequestBody TaskDTO.TaskRequest taskRequest,
                                                           @ApiIgnore @AuthenticationPrincipal User currentUser) {
        TaskDTO.TaskResponse taskResponse = taskService.createTask(projectId, taskRequest, currentUser);
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "태스크 상세 조회")
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO.TaskResponse> getTaskById(@PathVariable("id") Long id) {
        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "태스크 수정")
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO.TaskResponse> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskDTO.TaskRequest request,
                                                           @ApiIgnore @AuthenticationPrincipal User currentUser) {
        taskService.updateTask(id, request, currentUser);
        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "태스크 삭제")
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id,
                                                  @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = taskService.deleteTask(id, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "태스크 상태 변경")
    @PutMapping("/tasks/{id}/location")
    public ResponseEntity<TaskDTO.TaskResponse> updateTaskStatus(@PathVariable("id") Long id,
                                                                 @Valid @RequestBody TaskDTO.TaskDropLocation request,
                                                                 @ApiIgnore @AuthenticationPrincipal User currentUser) {
        taskService.updateTaskStatus(id, request, currentUser);
        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "프로젝트의 태스크 조회")
    @GetMapping("projects/{id}/tasks")
    public ResponseEntity<List<TaskDTO.TaskResponse>> getTasksByProject(@PathVariable("id") Long id) {
        List<TaskDTO.TaskResponse> taskList = taskService.getTasksByProject(id);
        return new ResponseEntity<>(taskList, HttpStatus.OK);
    }
}