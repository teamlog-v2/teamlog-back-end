package com.test.teamlog.controller;

import com.test.teamlog.entity.TaskStatus;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TaskDTO;
import com.test.teamlog.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        return new ResponseEntity<>(taskList, HttpStatus.OK);
    }

    // Create
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskDTO.TaskResponse> createTask(@PathVariable("projectId") Long projectId,
                                                       @Valid @RequestBody TaskDTO.TaskRequest taskRequest) {
        Long taskId = taskService.createTask(projectId, taskRequest);
        TaskDTO.TaskResponse taskResponse = taskService.getTask(taskId);
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }
//
    //Update
//    @PutMapping("/tasks/{id}")
//    public ResponseEntity<TaskDTO.TaskResponse> updateTaskStatus(@PathVariable("id") Long id, @Valid @RequestBody TaskDTO.TaskRequest request) {
//        taskService.updateTaskStatus(id, request);
//        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
//        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
//    }

    //Update
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO.TaskResponse> updateTaskStatus(@PathVariable("id") Long id, @Valid @RequestBody TaskDTO.TaskRequest request) {
        taskService.updateTask(id, request);
        TaskDTO.TaskResponse taskResponse = taskService.getTask(id);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    // Delete
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id) {
        ApiResponse apiResponse = taskService.deleteTask(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}