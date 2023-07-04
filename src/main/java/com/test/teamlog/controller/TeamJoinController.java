package com.test.teamlog.controller;

import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TeamJoinDTO;
import com.test.teamlog.service.TeamJoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "팀 초대 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TeamJoinController {
    private final TeamJoinService teamJoinService;

    @Operation(summary = "팀 멤버 초대(신청) 추가")
    @PostMapping("/teams/{teamId}/joins")
    public ResponseEntity<ApiResponse> inviteUserForTeam(@PathVariable("teamId") long teamId,
                                                         @RequestParam(value = "userId", required = false) String userId,
                                                         @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = null;
        // userId 있으면 초대, 없으면 신청
        if (userId != null) {
            apiResponse = teamJoinService.inviteUserForTeam(teamId, userId);
        } else {
            apiResponse = teamJoinService.applyForTeam(teamId, currentUser.getUser());
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "팀 멤버 초대(신청) 삭제")
    @DeleteMapping("/team-joins/{joinId}")
    public ResponseEntity<ApiResponse> deleteTeamJoin(@PathVariable("joinId") Long joinId) {
        ApiResponse apiResponse = teamJoinService.deleteTeamJoin(joinId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "팀 멤버 신청 목록 조회")
    @GetMapping("/teams/{id}/joins/apply")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForTeam>> getTeamApplyListForTeam(@PathVariable("id") Long id) {
        List<TeamJoinDTO.TeamJoinForTeam> response = teamJoinService.getTeamApplyListForTeam(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "팀 멤버 초대 목록 조회")
    @GetMapping("/teams/{id}/joins/invitation")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForTeam>> getTeamInvitationListForTeam(@PathVariable("id") Long id) {
        List<TeamJoinDTO.TeamJoinForTeam> response = teamJoinService.getTeamInvitationListForTeam(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저가 가입 신청한 팀 조회")
    @GetMapping("users/team-apply")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForUser>> getTeamApplyListForUser(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<TeamJoinDTO.TeamJoinForUser> response = teamJoinService.getTeamApplyListForUser(currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저가 받은 팀 초대 조회")
    @GetMapping("users/team-invitation")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForUser>> getTeamInvitationListForUser(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<TeamJoinDTO.TeamJoinForUser> response = teamJoinService.getTeamInvitationListForUser(currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}