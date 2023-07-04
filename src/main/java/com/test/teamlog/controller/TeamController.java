package com.test.teamlog.controller;

import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.TeamDTO;
import com.test.teamlog.service.ProjectService;
import com.test.teamlog.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "팀 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {
    private final TeamService teamService;
    private final ProjectService projectService;

    @Operation(summary = "팀 생성")
    @PostMapping
    public ResponseEntity<TeamDTO.TeamResponse> createTeam(@Valid @RequestBody TeamDTO.TeamRequest request,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        TeamDTO.TeamResponse response = teamService.createTeam(request, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "단일 팀 조회")
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO.TeamResponse> getTeamById(@PathVariable("id") long id,
                                                            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        TeamDTO.TeamResponse response = teamService.getTeam(id, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "팀 수정")
    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO.TeamResponse> updateTeam(@PathVariable("id") long id,
                                                           @Valid @RequestBody TeamDTO.TeamRequest request,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        TeamDTO.TeamResponse response = teamService.updateTeam(id, request, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "팀 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTeam(@PathVariable("id") Long id,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = teamService.deleteTeam(id, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "팀 마스터 위임")
    @PutMapping("/{id}/master")
    public ResponseEntity<ApiResponse> delegateTeamMaster(@PathVariable("id") long id,
                                                          @RequestParam(value = "new-master", required = true) String newMasterId,
                                                          @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = teamService.delegateTeamMaster(id, newMasterId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "팀 내 프로젝트 조회")
    @GetMapping("/{id}/projects")
    public ResponseEntity<List<ProjectDTO.ProjectListResponse>> getProjectsByTeam(@PathVariable("id") long id,
                                                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<ProjectDTO.ProjectListResponse> response = projectService.getProjectsByTeam(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "유저 팀 리스트 조회")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TeamDTO.TeamListResponse>> getTeamsByUser(@PathVariable("userId") String userId,
                                                                         @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<TeamDTO.TeamListResponse> response = teamService.getTeamsByUser(userId, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "팀 검색")
    @GetMapping
    public ResponseEntity<List<TeamDTO.TeamListResponse>> searchTeam(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                                     @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<TeamDTO.TeamListResponse> response = teamService.searchTeam(name, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}