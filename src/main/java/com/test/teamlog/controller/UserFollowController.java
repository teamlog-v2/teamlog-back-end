package com.test.teamlog.controller;

import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.service.UserFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserFollowController {
    private final UserFollowService userFollowService;

    @GetMapping("/users/{id}/follower")
    public ResponseEntity<List<UserDTO.UserFollowInfo>> getFollowerList(@PathVariable("id") String id) {
        long start = System.currentTimeMillis();
        List<UserDTO.UserFollowInfo> response = userFollowService.getFollowerList(id);
        long end = System.currentTimeMillis();
        System.out.println("수행시간: " + (end - start) + " ms");
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(31536000, TimeUnit.SECONDS))
                .body(response);
    }

    @GetMapping("/users/{id}/following")
    public ResponseEntity<List<UserDTO.UserFollowInfo>> getFollowingList(@PathVariable("id") String id) {
        List<UserDTO.UserFollowInfo> response = userFollowService.getFollowingList(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(31536000, TimeUnit.SECONDS))
                .body(response);
    }

    // Create
    @PostMapping("/userfollows")
    public ResponseEntity<ApiResponse> followUser(@RequestParam(value = "from-user", required = true) String fromUserId,
                                                  @RequestParam(value = "to-user", required = true) String toUserId) {
        ApiResponse apiResponse = userFollowService.followUser(fromUserId, toUserId);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/userfollows")
    public ResponseEntity<ApiResponse> unfollowUser(@RequestParam(value = "from-user", required = true) String fromUserId,
                                                    @RequestParam(value = "to-user", required = true) String toUserId) {
        ApiResponse apiResponse = userFollowService.unfollowUser(fromUserId, toUserId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
