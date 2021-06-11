package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.service.TeamMemberService;
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

@Api(tags = "팀 멤버 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @ApiOperation(value = "팀 멤버가 아닌 유저 조회")
    @GetMapping("/teams/{teamId}/not-members")
    public ResponseEntity<List<UserDTO.UserSimpleInfo>> getUsersNotInTeamMember(@PathVariable("teamId") Long teamId) {
        List<UserDTO.UserSimpleInfo> response = teamMemberService.getUsersNotInTeamMember(teamId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "팀 초대 수락 ( 메인용 )")
    @PostMapping("/teams/{teamId}/members")
    public ResponseEntity<ApiResponse> createProjectMember(@PathVariable("teamId") Long teamId,
                                                           @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = teamMemberService.createTeamMember(teamId, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "팀 초대 및 신청을 수락")
    @PostMapping("/team-joins/{joinId}")
    public ResponseEntity<ApiResponse> acceptTeamInvitation(@PathVariable("joinId") Long joinId) {
        ApiResponse apiResponse = teamMemberService.acceptTeamInvitation(joinId);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "팀 멤버 탈퇴/ 추방(userId 필요) : 임시")
    @DeleteMapping("/teams/{teamId}/members")
    public ResponseEntity<ApiResponse> leaveTeam(@PathVariable("teamId") long teamId,
                                                    @RequestParam(value = "userId", required = false) String userId,
                                                    @ApiIgnore  @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = null;
        // userId 없으면 탈퇴 있으면 추방
        if(userId == null) {
            apiResponse = teamMemberService.leaveTeam(teamId, currentUser);
        } else {
            apiResponse = teamMemberService.expelMember(teamId, userId, currentUser);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "팀 멤버 삭제 By TeamMember key")
    @DeleteMapping("/team-members/{id}")
    public ResponseEntity<ApiResponse> leaveTeam(@PathVariable("id") long id) {
        ApiResponse apiResponse = teamMemberService.deleteTeamMemeber(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "팀 멤버 조회")
    @GetMapping("/teams/{teamId}/members")
    public ResponseEntity<List<UserDTO.UserSimpleInfo>> getTeamMemberList(@PathVariable("teamId") Long teamId) {
        List<UserDTO.UserSimpleInfo> response = teamMemberService.getTeamMemberList(teamId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}