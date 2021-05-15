package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceForbiddenException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.*;
import com.test.teamlog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    // 팀 조회
    public TeamDTO.TeamResponse getTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
        TeamDTO.TeamResponse response = new TeamDTO.TeamResponse(team);
        return response;
    }

    // 사용자 팀 리스트 조회
    public List<TeamDTO.TeamListResponse> getTeamsByUser(String id) {
        // TODO : currentUser 들어올 경우 분기 ( 이는 사용자 프로젝트 리스트 조회도 동일 )
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("USER", "id", id));

        List<TeamMember> teams = teamMemberRepository.findByUser(user);

        List<TeamDTO.TeamListResponse> teamList = new ArrayList<>();
        for (TeamMember team : teams) {
            TeamDTO.TeamListResponse item = TeamDTO.TeamListResponse.builder()
                    .id(team.getTeam().getId())
                    .name(team.getTeam().getName())
                    .updateTime(team.getTeam().getUpdateTime())
                    .build();
            teamList.add(item);
        }
        return teamList;
    }

    // 팀 생성
    @Transactional
    public TeamDTO.TeamResponse createTeam(TeamDTO.TeamRequest request, User currentUser) {
        Team team = Team.builder()
                .name(request.getName())
                .introduction(request.getIntroduction())
                .accessModifier(request.getAccessModifier())
                .master(currentUser)
                .build();
        Team newTeam = teamRepository.save(team);

        TeamMember member = TeamMember.builder()
                .user(currentUser)
                .team(team)
                .build();
        teamMemberRepository.save(member);

        return new TeamDTO.TeamResponse(newTeam);
    }

    // 팀 수정 ( 위임 일단 포함 )
    @Transactional
    public TeamDTO.TeamResponse updateTeam(Long id, TeamDTO.TeamRequest request, User currentUser) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
        validateUserIsMaster(team, currentUser);

        team.setName(request.getName());
        team.setIntroduction(request.getIntroduction());
        team.setAccessModifier(request.getAccessModifier());

        if (request.getMasterId() != null) {
            User newMaster = userRepository.findById(request.getMasterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getMasterId()));
            team.setMaster(newMaster);
        }
        Team newTeam = teamRepository.save(team);

        return new TeamDTO.TeamResponse(newTeam);
    }

    // 팀 삭제
    @Transactional
    public ApiResponse deleteTeam(Long id, User currentUser) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
        validateUserIsMaster(team, currentUser);

        teamRepository.delete(team);
        return new ApiResponse(Boolean.TRUE, "팀 삭제 성공");
    }

    // ---------------------------
    // -------- 검증 메소드 --------
    // ---------------------------
    // 마스터 검증
    private void validateUserIsMaster(Team team, User currentUser) {
        if (team.getMaster().getId() != currentUser.getId())
            throw new ResourceForbiddenException("마스터 기능", currentUser.getId());
    }
//

}
