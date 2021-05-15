package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(description = "포스트 좋아요 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostLikeController {
    private final PostService postService;

    @ApiOperation(value = "해당 포스트를 좋아하는 사람 목록 조회")
    @GetMapping("/posts/{postId}/likers")
    public ResponseEntity<List<UserDTO.UserSimpleInfo>> getPostLikerList(@PathVariable("postId") long postId) {
        List<UserDTO.UserSimpleInfo> response = postService.getPostLikerList(postId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "포스트 좋아요")
    @PostMapping("/posts/{postId}/likers")
    public ResponseEntity<ApiResponse> likePost(@PathVariable("postId") long postId,
                                                @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = postService.likePost(postId, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "포스트 좋아요 취소")
    @DeleteMapping("/posts/{postId}/likers")
    public ResponseEntity<ApiResponse> unlikePost(@PathVariable("postId") long postId,
                                                  @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = postService.unlikePost(postId, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}