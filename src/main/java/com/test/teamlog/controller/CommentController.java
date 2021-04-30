package com.test.teamlog.controller;

import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.CommentDTO;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.service.CommentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @ApiOperation(value = "댓글 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDTO.CommentResponse>> getProjectById(@PathVariable("postId") long postId) {
        List<CommentDTO.CommentResponse> response = commentService.getComments(postId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "댓글 생성")
    @PostMapping
    public ResponseEntity<ApiResponse> createProject(@RequestBody CommentDTO.CommentRequest request) {
        ApiResponse apiResponse = commentService.createComment(request);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "댓글 수정")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProject(@PathVariable("id") long id, @RequestBody CommentDTO.CommentRequest request) {
        ApiResponse apiResponse = commentService.updateComment(id, request);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "댓글 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id) {
        ApiResponse apiResponse = commentService.deleteComment(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}