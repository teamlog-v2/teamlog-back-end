package com.test.teamlog.domain.task.controller;

import com.test.teamlog.domain.task.dto.*;
import com.test.teamlog.domain.task.service.TaskService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.security.AccountAdapter;
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
    private final TaskService taskServiceV2;

    @Operation(summary = "태스크 생성")
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskCreateResponse> create(@PathVariable("projectId") Long projectId,
                                                     @Valid @RequestBody TaskCreateRequest request,
                                                     @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        final TaskCreateResult result = taskServiceV2.create(request.toInput(projectId), currentAccount.getAccount());

        return new ResponseEntity<>(TaskCreateResponse.from(result), HttpStatus.CREATED);
    }

    @Operation(summary = "태스크 수정")
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskUpdateResponse> update(@PathVariable("id") Long id,
                                                     @Valid @RequestBody TaskUpdateRequest request,
                                                     @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        final TaskUpdateResult result = taskServiceV2.update(request.toInput(id), currentAccount.getAccount());

        return new ResponseEntity<>(TaskUpdateResponse.from(result), HttpStatus.OK);
    }

    @Operation(summary = "태스크 상세 조회")
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskReadDetailResponse> readOne(@PathVariable("id") Long id) {
        final TaskReadDetailResult result = taskServiceV2.readOne(id);

        return new ResponseEntity<>(TaskReadDetailResponse.from(result), HttpStatus.OK);
    }

    @Operation(summary = "태스크 삭제")
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id,
                                              @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        final boolean result = taskServiceV2.delete(id, currentAccount.getAccount());
        return new ResponseEntity<>(new ApiResponse(result), HttpStatus.OK);
    }

    // TODO: 기획 변경에 따른 메소드 변경
    @Operation(summary = "태스크 상태 변경")
    @PutMapping("/tasks/{id}/location")
    public ResponseEntity<TaskReadDetailResponse> updateStatus(@PathVariable("id") Long id,
                                                               @Valid @RequestBody TaskUpdateStatusRequest request,
                                                               @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        taskServiceV2.updateStatus(id, currentAccount.getAccount(), request.getStatus());
        final TaskReadDetailResult result = taskServiceV2.readOne(id);

        return new ResponseEntity<>(TaskReadDetailResponse.from(result), HttpStatus.OK);
    }

    @Operation(summary = "프로젝트의 태스크 조회")
    @GetMapping("projects/{id}/tasks")
    public ResponseEntity<List<TaskReadByProjectResponse>> readAllByProject(@PathVariable("id") Long id) {
        final List<TaskReadByProjectResult> resultList = taskServiceV2.readAllByProject(id);
        final List<TaskReadByProjectResponse> responseList = resultList.stream().map(TaskReadByProjectResponse::from).toList();

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}