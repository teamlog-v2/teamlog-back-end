package com.test.teamlog.controller;

import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TaskDTO;
import com.test.teamlog.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "태스크 관리")
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "태스크 생성")
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskDTO.TaskResponse> createTask(@PathVariable("projectId") Long projectId,
                                                           @Valid @RequestBody TaskDTO.TaskRequest taskRequest,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        TaskDTO.TaskResponse taskResponse = taskService.createTask(projectId, taskRequest, currentUser.getUser());
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "태스크 상세 조회")
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO.TaskResponse> getTaskById(@PathVariable("id") Long id) {
        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @Operation(summary = "태스크 수정")
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO.TaskResponse> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskDTO.TaskRequest request,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        taskService.updateTask(id, request, currentUser.getUser());
        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @Operation(summary = "태스크 삭제")
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = taskService.deleteTask(id, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "태스크 상태 변경")
    @PutMapping("/tasks/{id}/location")
    public ResponseEntity<TaskDTO.TaskResponse> updateTaskStatus(@PathVariable("id") Long id,
                                                                 @Valid @RequestBody TaskDTO.TaskDropLocation request,
                                                                 @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        taskService.updateTaskStatus(id, request, currentUser.getUser());
        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트의 태스크 조회")
    @GetMapping("projects/{id}/tasks")
    public ResponseEntity<List<TaskDTO.TaskResponse>> getTasksByProject(@PathVariable("id") Long id) {
        List<TaskDTO.TaskResponse> taskList = taskService.getTasksByProject(id);
        return new ResponseEntity<>(taskList, HttpStatus.OK);
    }
}