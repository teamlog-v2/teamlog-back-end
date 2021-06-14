package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.BadRequestException;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceForbiddenException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.*;
import com.test.teamlog.repository.TeamJoinRepository;
import com.test.teamlog.repository.TeamMemberRepository;
import com.test.teamlog.repository.TeamRepository;
import com.test.teamlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamJoinService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamJoinRepository teamJoinRepository;
    private final TeamService teamService;

    // 팀 멤버 초대
    @Transactional
    public ApiResponse inviteUserForTeam(Long teamId, String userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (teamService.isUserMemberOfTeam(team, user))
            throw new ResourceAlreadyExistsException("이미 해당 팀의 멤버입니다.");

        if (teamService.isJoinAlreadyExist(team, user))
            throw new ResourceAlreadyExistsException("이미 해당 팀에 멤버 신청 혹은 초대가 존재합니다.");

        TeamJoin teamJoin = TeamJoin.builder()
                .team(team)
                .user(user)
                .isInvited(Boolean.TRUE)
                .isAccepted(Boolean.FALSE)
                .build();
        teamJoinRepository.save(teamJoin);

        return new ApiResponse(Boolean.TRUE, "유저 : " + user.getName() + " 초대 완료");
    }

    // 팀 멤버 신청
    @Transactional
    public ApiResponse applyForTeam(Long teamId, User currentUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));

        if (teamService.isUserMemberOfTeam(team, currentUser))
            throw new ResourceAlreadyExistsException("이미 해당 팀의 멤버입니다.");

        if (teamService.isJoinAlreadyExist(team, currentUser))
            throw new ResourceAlreadyExistsException("이미 해당 팀에 멤버 신청 혹은 초대가 존재합니다.");

        TeamJoin teamJoin = TeamJoin.builder()
                .team(team)
                .user(currentUser)
                .isAccepted(Boolean.TRUE)
                .isInvited(Boolean.FALSE)
                .build();
        teamJoinRepository.save(teamJoin);

        return new ApiResponse(Boolean.TRUE, "팀 멤버 신청 완료");
    }

    // 팀 멤버 신청 삭제
    @Transactional
    public ApiResponse deleteTeamJoin(Long teamJoinId) {
        TeamJoin teamJoin = teamJoinRepository.findById(teamJoinId)
                .orElseThrow(() -> new ResourceNotFoundException("TeamJoin", "id", teamJoinId));

        teamJoinRepository.delete(teamJoin);

        return new ApiResponse(Boolean.TRUE, "팀 멤버 신청 삭제 완료");
    }

    // 팀 멤버 신청자 목록 조회
    public List<TeamJoinDTO.TeamJoinForTeam> getTeamApplyListForTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));

        List<TeamJoin> teamJoins = teamJoinRepository.findAllByTeamAndIsAcceptedTrueAndIsInvitedFalse(team);

        List<TeamJoinDTO.TeamJoinForTeam> response = new ArrayList<>();
        for (TeamJoin join : teamJoins) {
            UserDTO.UserSimpleInfo user = new UserDTO.UserSimpleInfo(join.getUser());
            TeamJoinDTO.TeamJoinForTeam temp = TeamJoinDTO.TeamJoinForTeam.builder()
                    .id(join.getId())
                    .teamName(join.getTeam().getName())
                    .user(user)
                    .build();
            response.add(temp);
        }
        return response;
    }

    // 팀 멤버로 초대한 사용자 목록 조회
    public List<TeamJoinDTO.TeamJoinForTeam> getTeamInvitationListForTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));

        List<TeamJoin> teamJoins = teamJoinRepository.findAllByTeamAndIsAcceptedFalseAndIsInvitedTrue(team);

        List<TeamJoinDTO.TeamJoinForTeam> response = new ArrayList<>();
        for (TeamJoin join : teamJoins) {
            UserDTO.UserSimpleInfo user = new UserDTO.UserSimpleInfo(join.getUser());
            TeamJoinDTO.TeamJoinForTeam temp = TeamJoinDTO.TeamJoinForTeam.builder()
                    .id(join.getId())
                    .teamName(join.getTeam().getName())
                    .user(user)
                    .build();
            response.add(temp);
        }
        return response;
    }


    // 유저가 가입 신청한 팀 목록 조회
    public List<TeamJoinDTO.TeamJoinForUser> getTeamApplyListForUser(User currentUser) {
        List<TeamJoin> teamJoins = teamJoinRepository.findAllByUserAndIsAcceptedTrueAndIsInvitedFalse(currentUser);

        List<TeamJoinDTO.TeamJoinForUser> response = new ArrayList<>();
        for (TeamJoin join : teamJoins) {
            TeamJoinDTO.TeamJoinForUser temp = TeamJoinDTO.TeamJoinForUser.builder()
                    .id(join.getId())
                    .teamId(join.getTeam().getId())
                    .teamName(join.getTeam().getName())
                    .build();
            response.add(temp);
        }
        return response;
    }

    // 유저가 받은 팀 초대 조회
    public List<TeamJoinDTO.TeamJoinForUser> getTeamInvitationListForUser(User currentUser) {
        List<TeamJoin> teamJoins = teamJoinRepository.findAllByUserAndIsAcceptedFalseAndIsInvitedTrue(currentUser);

        List<TeamJoinDTO.TeamJoinForUser> response = new ArrayList<>();
        for (TeamJoin join : teamJoins) {
            TeamJoinDTO.TeamJoinForUser temp = TeamJoinDTO.TeamJoinForUser.builder()
                    .id(join.getId())
                    .teamId(join.getTeam().getId())
                    .teamName(join.getTeam().getName())
                    .build();
            response.add(temp);
        }
        return response;
    }

}