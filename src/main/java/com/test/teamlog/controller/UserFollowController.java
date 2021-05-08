package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.service.UserFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserFollowController {
    private final UserFollowService userFollowService;

    @GetMapping("/users/{id}/follower")
    public ResponseEntity<List<UserDTO.UserFollowInfo>> getFollowerList(@PathVariable("id") String id,
                                                                        @AuthenticationPrincipal User currentUser) {
        List<UserDTO.UserFollowInfo> response = userFollowService.getFollowerList(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1800, TimeUnit.SECONDS))
                .body(response);
    }

    @GetMapping("/users/{id}/following")
    public ResponseEntity<List<UserDTO.UserFollowInfo>> getFollowingList(@PathVariable("id") String id,
                                                                         @AuthenticationPrincipal User currentUser) {
        List<UserDTO.UserFollowInfo> response = userFollowService.getFollowingList(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1800, TimeUnit.SECONDS))
                .body(response);
    }

    @PostMapping("/userfollows")
    public ResponseEntity<ApiResponse> followUser(@RequestParam(value = "from-user", required = true) String fromUserId,
                                                  @RequestParam(value = "to-user", required = true) String toUserId,
                                                  @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = userFollowService.followUser(fromUserId, toUserId);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/userfollows")
    public ResponseEntity<ApiResponse> unfollowUser(@RequestParam(value = "from-user", required = true) String fromUserId,
                                                    @RequestParam(value = "to-user", required = true) String toUserId,
                                                    @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = userFollowService.unfollowUser(fromUserId, toUserId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
