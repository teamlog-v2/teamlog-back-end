package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TeamJoinDTO;
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

@Api(description = "팀 초대 관리 컨트롤러( 팀 초대 수락은 여기있음 )")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TeamJoinController {
    private final TeamService teamService;

    @ApiOperation(value = "팀 멤버 초대(userId 필요) 및 신청")
    @PostMapping("/teams/{teamId}/joins")
    public ResponseEntity<ApiResponse> inviteUserForTeam(@PathVariable("teamId") long teamId,
                                                            @RequestParam(value = "userId", required = false) String userId,
                                                            @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = null;
        // userId 있으면 초대, 없으면 신청
        if (userId != null) {
            apiResponse = teamService.inviteUserForTeam(teamId, userId);
        } else {
            apiResponse = teamService.applyForTeam(teamId, currentUser);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "팀 멤버 신청 삭제")
    @DeleteMapping("/team-joins/{joinId}")
    public ResponseEntity<ApiResponse> deleteTeamJoin(@PathVariable("joinId") Long joinId) {
        ApiResponse apiResponse = teamService.deleteTeamJoin(joinId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "팀 멤버 신청자 목록 조회 (팀 관리)")
    @GetMapping("/teams/{id}/joins/apply")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForTeam>> getTeamApplyListForTeam(@PathVariable("id") Long id) {
        List<TeamJoinDTO.TeamJoinForTeam> response = teamService.getTeamApplyListForTeam(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "팀 멤버로 초대한 사용자 목록 조회 (팀 관리)")
    @GetMapping("/teams/{id}/joins/invitation")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForTeam>> getTeamInvitationListForTeam(@PathVariable("id") Long id) {
        List<TeamJoinDTO.TeamJoinForTeam> response = teamService.getTeamInvitationListForTeam(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "유저가 받은 팀 초대 조회")
    @GetMapping("users/team-invitation")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForUser>> getTeamInvitationListForUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<TeamJoinDTO.TeamJoinForUser> response = teamService.getTeamInvitationListForUser(currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "유저가 가입 신청한 팀 조회")
    @GetMapping("users/team-apply")
    public ResponseEntity<List<TeamJoinDTO.TeamJoinForUser>> getTeamApplyListForUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<TeamJoinDTO.TeamJoinForUser> response = teamService.getTeamApplyListForUser(currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}