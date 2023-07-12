package com.test.teamlog.domain.comment.controller;

import com.test.teamlog.domain.comment.dto.CommentCreateRequest;
import com.test.teamlog.domain.comment.dto.CommentInfoResponse;
import com.test.teamlog.domain.comment.dto.CommentUpdateRequest;
import com.test.teamlog.domain.comment.service.CommentService;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "댓글 관리")
public class CommentApiController {
    private final CommentService commentService;

    @Operation(summary = "댓글 생성")
    @PostMapping("/comments")
    public ResponseEntity<ApiResponse> create(@RequestBody CommentCreateRequest request,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = commentService.create(request.toInput(), currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/comments/{id}")
    public ResponseEntity<ApiResponse> updateProject(@PathVariable("id") long id,
                                                     @RequestBody CommentUpdateRequest request,
                                                     @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = commentService.update(id, request.toInput(), currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = commentService.deleteComment(id, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "부모 댓글 조회")
    @GetMapping("/posts/{postId}/parent-comments")
    public ResponseEntity<PagedResponse<CommentInfoResponse>> getParentCommentsByPost(@PathVariable("postId") long postId,
                                                                                      @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                      @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                                                      @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final PagedResponse<CommentInfoResponse> response = commentService.getParentComments(postId, page, size, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "대댓글 조회")
    @GetMapping("/comments/{commentId}/child-comments")
    public ResponseEntity<PagedResponse<CommentInfoResponse>> getChildComments(@PathVariable("commentId") long commentId,
                                                                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                               @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                                               @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        PagedResponse<CommentInfoResponse> response = commentService.getChildComments(commentId, page, size, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}