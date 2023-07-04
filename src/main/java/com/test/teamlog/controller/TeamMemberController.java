package com.test.teamlog.controller;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.service.TeamMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "팀 멤버 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @Operation(summary = "팀 초대 수락 ( 메인용 )")
    @PostMapping("/teams/{teamId}/members")
    public ResponseEntity<ApiResponse> createProjectMember(@PathVariable("teamId") Long teamId,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = teamMemberService.createTeamMember(teamId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "팀 초대 및 신청을 수락")
    @PostMapping("/team-joins/{joinId}")
    public ResponseEntity<ApiResponse> acceptTeamInvitation(@PathVariable("joinId") Long joinId) {
        ApiResponse apiResponse = teamMemberService.acceptTeamInvitation(joinId);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "팀 멤버 삭제")
    @DeleteMapping("/teams/{teamId}/members")
    public ResponseEntity<ApiResponse> leaveTeam(@PathVariable("teamId") long teamId,
                                                    @RequestParam(value = "userId", required = false) String userId,
                                                    @Parameter(hidden = true)  @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = null;
        // userId 없으면 탈퇴 있으면 추방
        if(userId == null) {
            apiResponse = teamMemberService.leaveTeam(teamId, currentUser.getUser());
        } else {
            apiResponse = teamMemberService.expelMember(teamId, userId, currentUser.getUser());
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "팀 멤버 조회")
    @GetMapping("/teams/{teamId}/members")
    public ResponseEntity<List<UserRequest.UserSimpleInfo>> getTeamMemberList(@PathVariable("teamId") Long teamId) {
        List<UserRequest.UserSimpleInfo> response = teamMemberService.getTeamMemberList(teamId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "팀 멤버가 아닌 유저 조회")
    @GetMapping("/teams/{teamId}/not-members")
    public ResponseEntity<List<UserRequest.UserSimpleInfo>> getUsersNotInTeamMember(@PathVariable("teamId") Long teamId) {
        List<UserRequest.UserSimpleInfo> response = teamMemberService.getUsersNotInTeamMember(teamId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}