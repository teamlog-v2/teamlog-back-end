package com.app.teamlog.domain.comment.controller;

import com.app.teamlog.domain.comment.dto.*;
import com.app.teamlog.domain.comment.service.CommentService;
import com.app.teamlog.global.dto.ApiResponse;
import com.app.teamlog.global.dto.CustomPageRequest;
import com.app.teamlog.global.dto.PagedResponse;
import com.app.teamlog.global.security.AccountAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "댓글 관리")
public class CommentApiController {
    private final CommentService commentService;

    @Operation(summary = "댓글 생성")
    @PostMapping("/comments")
    public ResponseEntity<CommentCreateResponse> create(@RequestBody CommentCreateRequest request,
                                                        @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        final CommentCreateResult result = commentService.create(request.toInput(), currentAccount.getAccount());
        return new ResponseEntity<>(CommentCreateResponse.of(result), HttpStatus.CREATED);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/comments/{id}")
    public ResponseEntity<ApiResponse> updateProject(@PathVariable("id") long id,
                                                     @RequestBody CommentUpdateRequest request,
                                                     @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        ApiResponse apiResponse = commentService.update(id, request.toInput(), currentAccount.getAccount());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        ApiResponse apiResponse = commentService.deleteComment(id, currentAccount.getAccount());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "댓글 조회")
    @GetMapping("/posts/{postId}/parent-comments")
    public ResponseEntity<PagedResponse<CommentInfoResponse>> readCommentListByPost(@PathVariable("postId") long postId,
                                                                                    @ModelAttribute CustomPageRequest pageRequest,
                                                                                    @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        final PagedResponse<CommentInfoResponse> response = commentService.readCommentListByPostId(postId, pageRequest.toPageRequest(), currentAccount.getAccount());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "대댓글 조회")
    @GetMapping("/comments/{commentId}/child-comments")
    public ResponseEntity<PagedResponse<CommentInfoResponse>> getChildComments(@PathVariable("commentId") long commentId,
                                                                               @ModelAttribute CustomPageRequest pageRequest,
                                                                               @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        PagedResponse<CommentInfoResponse> response = commentService.readChildCommentList(commentId, pageRequest.toPageRequest(), currentAccount.getAccount());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "개인 작성 이력 조회 (댓글)")
    @GetMapping("/accounts/comments")
    public ResponseEntity<List<CommentInfoResponse>> getCommentsByAccount(@Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        List<CommentInfoResponse> response = null;

        if (currentAccount == null) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            response = commentService.getCommentByAccount(currentAccount.getAccount());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}