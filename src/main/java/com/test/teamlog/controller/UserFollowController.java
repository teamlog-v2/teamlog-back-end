package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.service.UserFollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api(tags = "유저 팔로우 관리")
public class UserFollowController {
    private final UserFollowService userFollowService;

    @ApiOperation(value = "유저 팔로우")
    @PostMapping("/userfollows/{targetId}")
    public ResponseEntity<ApiResponse> followUser(@PathVariable("targetId") String toUserId,
                                                  @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = userFollowService.followUser(currentUser, toUserId);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "유저 언팔로우")
    @DeleteMapping("/userfollows/{targetId}")
    public ResponseEntity<ApiResponse> unfollowUser(@PathVariable("targetId") String fromUserId,
                                                    @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = userFollowService.unfollowUser(fromUserId, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "유저 팔로워 조회")
    @GetMapping("/users/{id}/follower")
    public ResponseEntity<List<UserDTO.UserFollowInfo>> getFollowerList(@PathVariable("id") String id,
                                                                        @ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<UserDTO.UserFollowInfo> response = userFollowService.getFollowerList(id, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "유저 팔로잉 조회")
    @GetMapping("/users/{id}/following")
    public ResponseEntity<List<UserDTO.UserFollowInfo>> getFollowingList(@PathVariable("id") String id,
                                                                         @ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<UserDTO.UserFollowInfo> response = userFollowService.getFollowingList(id, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
