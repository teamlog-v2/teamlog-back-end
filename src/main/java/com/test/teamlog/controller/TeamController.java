package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TeamDTO;
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

@Api(description = "팀 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {
    private final TeamService teamService;

    @ApiOperation(value = "단일 팀 조회")
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO.TeamResponse> getTeamById(@PathVariable("id") long id) {
        TeamDTO.TeamResponse response = teamService.getTeam(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "유저 팀 리스트 조회")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TeamDTO.TeamListResponse>> getTeamsByUser(@PathVariable("userId") String userId) {
        List<TeamDTO.TeamListResponse> response = teamService.getTeamsByUser(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "팀 생성")
    @PostMapping
    public ResponseEntity<TeamDTO.TeamResponse> createTeam(@RequestBody TeamDTO.TeamRequest request,
                                                           @ApiIgnore @AuthenticationPrincipal User currentUser) {
        TeamDTO.TeamResponse response = teamService.createTeam(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "팀 수정")
    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO.TeamResponse> updateTeam(@PathVariable("id") long id,
                                                           @RequestBody TeamDTO.TeamRequest request,
                                                           @ApiIgnore @AuthenticationPrincipal User currentUser) {
        TeamDTO.TeamResponse response = teamService.updateTeam(id, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "팀 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTeam(@PathVariable("id") Long id,
                                                  @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = teamService.deleteTeam(id, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}