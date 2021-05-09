package com.test.teamlog.controller;

import com.test.teamlog.entity.TaskStatus;
import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TaskDTO;
import com.test.teamlog.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;

    // Read
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO.TaskResponse> getTaskById(@PathVariable("id") Long id) {
        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    // Read
    @GetMapping("projects/{id}/tasks")
    public ResponseEntity<List<TaskDTO.TaskResponse>> getTasksByProject(@PathVariable("id") Long id) {
        List<TaskDTO.TaskResponse> taskList = taskService.getTasksByProject(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .body(taskList);
    }

    // Create
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskDTO.TaskResponse> createTask(@PathVariable("projectId") Long projectId,
                                                           @Valid @RequestBody TaskDTO.TaskRequest taskRequest,
                                                           @AuthenticationPrincipal User currentUser) {
        Long taskId = taskService.createTask(projectId, taskRequest);
        TaskDTO.TaskResponse taskResponse = taskService.getTask(taskId);
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO.TaskResponse> updateTaskStatus(@PathVariable("id") Long id,
                                                                 @Valid @RequestBody TaskDTO.TaskRequest request,
                                                                 @AuthenticationPrincipal User currentUser) {
        taskService.updateTaskStatus(id, request);
        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

//    //Update
//    @PutMapping("/tasks/{id}")
//    public ResponseEntity<TaskDTO.TaskResponse> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskDTO.TaskRequest request) {
//        taskService.updateTask(id, request);
//        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
//        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
//    }

    // Delete
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id,
                                                  @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = taskService.deleteTask(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}