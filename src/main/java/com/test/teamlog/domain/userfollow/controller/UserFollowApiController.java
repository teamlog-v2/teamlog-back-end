package com.test.teamlog.domain.userfollow.controller;

import com.test.teamlog.domain.userfollow.dto.UserFollowerReadResponse;
import com.test.teamlog.domain.userfollow.dto.UserFollowerReadResult;
import com.test.teamlog.domain.userfollow.dto.UserFollowingReadResponse;
import com.test.teamlog.domain.userfollow.dto.UserFollowingReadResult;
import com.test.teamlog.domain.userfollow.service.UserFollowService;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "유저 팔로우 관리")
public class UserFollowApiController {
    private final UserFollowService userFollowService;

    @Operation(summary = "유저 팔로우")
    @PostMapping("/accountfollows/{targetId}")
    public ResponseEntity<ApiResponse> follow(@PathVariable("targetId") String toUserId,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = userFollowService.follow(currentUser.getUser(), toUserId);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "유저 언팔로우")
    @DeleteMapping("/accountfollows/{targetId}")
    public ResponseEntity<ApiResponse> unfollow(@PathVariable("targetId") String fromUserId,
                                                @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = userFollowService.unfollow(fromUserId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "유저 팔로워 조회")
    @GetMapping("/accounts/{id}/follower")
    public ResponseEntity<List<UserFollowerReadResponse>> readAllFollower(@PathVariable("id") String identification,
                                                                            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final List<UserFollowerReadResult> resultList = userFollowService.readAllFollower(identification, currentUser.getUser());
        final List<UserFollowerReadResponse> responseList = resultList.stream().map(UserFollowerReadResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(summary = "유저 팔로잉 조회")
    @GetMapping("/accounts/{id}/following")
    public ResponseEntity<List<UserFollowingReadResponse>> readAllFollowing(@PathVariable("id") String id,
                                                                             @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final List<UserFollowingReadResult> resultList = userFollowService.readAllFollowing(id, currentUser.getUser());
        final List<UserFollowingReadResponse> responseList = resultList.stream().map(UserFollowingReadResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
