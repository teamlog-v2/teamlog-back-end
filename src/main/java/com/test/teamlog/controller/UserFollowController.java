package com.test.teamlog.controller;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.service.UserFollowService;
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
@Tag(name = "유저 팔로우 관리")
public class UserFollowController {
    private final UserFollowService userFollowService;

    @Operation(summary = "유저 팔로우")
    @PostMapping("/accountfollows/{targetId}")
    public ResponseEntity<ApiResponse> followUser(@PathVariable("targetId") String toUserId,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = userFollowService.followUser(currentUser.getUser(), toUserId);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "유저 언팔로우")
    @DeleteMapping("/accountfollows/{targetId}")
    public ResponseEntity<ApiResponse> unfollowUser(@PathVariable("targetId") String fromUserId,
                                                    @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = userFollowService.unfollowUser(fromUserId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "유저 팔로워 조회")
    @GetMapping("/accounts/{id}/follower")
    public ResponseEntity<List<UserRequest.UserFollowInfo>> getFollowerList(@PathVariable("id") String id,
                                                                            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<UserRequest.UserFollowInfo> response = userFollowService.getFollowerList(id, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저 팔로잉 조회")
    @GetMapping("/accounts/{id}/following")
    public ResponseEntity<List<UserRequest.UserFollowInfo>> getFollowingList(@PathVariable("id") String id,
                                                                             @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<UserRequest.UserFollowInfo> response = userFollowService.getFollowingList(id, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
