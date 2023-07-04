package com.test.teamlog.controller;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TeamDTO;
import com.test.teamlog.service.TeamFollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "팀 팔로우 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TeamFollowController {
    private final TeamFollowService teamFollowService;

    @Operation(summary = "팀 팔로우")
    @PostMapping("/teams/{teamId}/followers")
    public ResponseEntity<ApiResponse> followTeam(@PathVariable("teamId") Long teamId,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = teamFollowService.followTeam(teamId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "팀 언팔로우")
    @DeleteMapping("/teams/{teamId}/followers")
    public ResponseEntity<ApiResponse> unfollowTeam(@PathVariable("teamId") Long teamId,
                                                    @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = teamFollowService.unfollowTeam(teamId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "팀 팔로워 조회")
    @GetMapping("/teams/{teamId}/followers")
    public ResponseEntity<List<UserRequest.UserSimpleInfo>> getTeamFollowerList(@PathVariable("teamId") Long teamId) {
        List<UserRequest.UserSimpleInfo> response = teamFollowService.getTeamFollowerList(teamId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저가 팔로우하는 팀 조회")
    @GetMapping("/users/{userId}/team-follow")
    public ResponseEntity<List<TeamDTO.TeamListResponse>> getFollowingTeamListByUser(@PathVariable("userId") String userId) {
        List<TeamDTO.TeamListResponse> response = teamFollowService.getTeamListByTeamFollower(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
