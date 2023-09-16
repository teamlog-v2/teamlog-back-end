package com.test.teamlog.domain.postlike.controller;

import com.test.teamlog.domain.postlike.dto.PostLikerResponse;
import com.test.teamlog.domain.postlike.dto.PostLikerResult;
import com.test.teamlog.domain.postlike.service.PostLikeService;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "게시물 좋아요 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostLikeApiController {
    private final PostLikeService postService;

    @Operation(summary = "게시물 좋아요")
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse> like(@PathVariable("postId") long postId,
                                            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = postService.create(postId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "게시물 좋아요 취소")
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse> unlike(@PathVariable("postId") long postId,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = postService.delete(postId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "게시물을 좋아하는 사람 조회")
    @GetMapping("/{postId}/likers")
    public ResponseEntity<List<PostLikerResponse>> readPostLikerList(@PathVariable("postId") long postId) {
        List<PostLikerResult> resultList = postService.readPostLikerList(postId);
        final List<PostLikerResponse> response = resultList.stream().map(PostLikerResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
