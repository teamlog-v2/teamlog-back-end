package com.test.teamlog.controller;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.domain.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시물 좋아요 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostLikeController {
    private final PostService postService;

    @Operation(summary = "게시물 좋아요")
    @PostMapping("/posts/{postId}/likers")
    public ResponseEntity<ApiResponse> likePost(@PathVariable("postId") long postId,
                                                @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = postService.likePost(postId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "게시물 좋아요 취소")
    @DeleteMapping("/posts/{postId}/likers")
    public ResponseEntity<ApiResponse> unlikePost(@PathVariable("postId") long postId,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = postService.unlikePost(postId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "게시물을 좋아하는 사람 조회")
    @GetMapping("/posts/{postId}/likers")
    public ResponseEntity<List<UserRequest.UserSimpleInfo>> getPostLikerList(@PathVariable("postId") long postId) {
        List<UserRequest.UserSimpleInfo> response = postService.getPostLikerList(postId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}