package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TeamJoinDTO;
import com.test.teamlog.service.TeamJoinService;
import com.test.teamlog.service.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(tags = "팀 초대 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TeamJoinController {
    private final TeamJoinService teamJoinService;

    @ApiOperation(value = "팀 멤버 초대(신청) 추가")
    @PostMapping("/teams/{teamId}/joins")
    public ResponseEntity<ApiResponse> inviteUserForTeam(@PathVariable("teamId") long teamId,
                                                         @RequestParam(value = "userId", required = false) String userId,
                                                         @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = null;
        // userId 있으면 초대, 없으면 신청
        if (userId != null) {
            apiResponse = teamJoinService.inviteUserForTeam(teamId, userId);
        } else {
            apiResponse = teamJoinService.applyForTeam(teamId, currentUser);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "팀 멤버 초대(신청) 삭제")
    @DeleteMapping("/team-joins/{joinId}")
    public ResponseEntity<ApiResponse> deleteTeamJoin(@PathVariable("joinId") Long joinId) {
        ApiResponse apiResponse = teamJoinService.deleteTeamJoin(joinId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "팀 멤버 신청 목록 조회")
    @GetMapping("/teams/{id}/joins/apply")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForTeam>> getTeamApplyListForTeam(@PathVariable("id") Long id) {
        List<TeamJoinDTO.TeamJoinForTeam> response = teamJoinService.getTeamApplyListForTeam(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "팀 멤버 초대 목록 조회")
    @GetMapping("/teams/{id}/joins/invitation")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForTeam>> getTeamInvitationListForTeam(@PathVariable("id") Long id) {
        List<TeamJoinDTO.TeamJoinForTeam> response = teamJoinService.getTeamInvitationListForTeam(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "유저가 가입 신청한 팀 조회")
    @GetMapping("users/team-apply")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForUser>> getTeamApplyListForUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<TeamJoinDTO.TeamJoinForUser> response = teamJoinService.getTeamApplyListForUser(currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "유저가 받은 팀 초대 조회")
    @GetMapping("users/team-invitation")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForUser>> getTeamInvitationListForUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<TeamJoinDTO.TeamJoinForUser> response = teamJoinService.getTeamInvitationListForUser(currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}