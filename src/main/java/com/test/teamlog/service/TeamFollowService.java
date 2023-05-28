package com.test.teamlog.service;

import com.test.teamlog.domain.account.model.User;

import com.test.teamlog.domain.account.repository.UserRepository;
import com.test.teamlog.entity.Team;
import com.test.teamlog.entity.TeamFollower;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TeamDTO;
import com.test.teamlog.domain.account.dto.UserDTO;
import com.test.teamlog.repository.ProjectRepository;
import com.test.teamlog.repository.TeamFollowerRepository;
import com.test.teamlog.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamFollowService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TeamFollowerRepository teamFollowerRepository;

    // 유저가 팔로우하는 팀 목록 조회
    public List<TeamDTO.TeamListResponse> getTeamListByTeamFollower(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
        List<TeamFollower> teamFollowers = teamFollowerRepository.findAllByUser(user);

        List<TeamDTO.TeamListResponse> teamList = new ArrayList<>();
        for (TeamFollower follower : teamFollowers) {
            Team team = follower.getTeam();
            long projectCount = projectRepository.getProjectCount(team);
            TeamDTO.TeamListResponse temp = TeamDTO.TeamListResponse.builder()
                    .id(team.getId())
                    .name(team.getName())
                    .projectCount(projectCount)
                    .updateTime(team.getUpdateTime())
                    .build();
            teamList.add(temp);
        }

        return teamList;
    }

    // 해당 팀을 팔로우하는 사용자 목록 조회
    public List<UserDTO.UserSimpleInfo> getTeamFollowerList(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "ID", teamId));

        List<TeamFollower> teamFollowers = teamFollowerRepository.findAllByTeam(team);

        List<UserDTO.UserSimpleInfo> userList = new ArrayList<>();
        for (TeamFollower follower : teamFollowers) {
            userList.add(new UserDTO.UserSimpleInfo(follower.getUser()));
        }
        return userList;
    }

    // 팀 팔로우
    @Transactional
    public ApiResponse followTeam(Long teamId, User currentUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "ID", teamId));
        TeamFollower newFollow = TeamFollower.builder()
                .team(team)
                .user(currentUser)
                .build();

        try {
            teamFollowerRepository.saveAndFlush(newFollow);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("이미 해당 팀을 팔로우 하고 있습니다.");
        }

        return new ApiResponse(Boolean.TRUE, "팀 팔로우 성공");
    }

    // 팀 언팔로우
    @Transactional
    public ApiResponse unfollowTeam(Long teamId, User currentUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "ID", teamId));
        TeamFollower teamFollower = teamFollowerRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("TeamFollwer", "UserId", currentUser.getId()));
        teamFollowerRepository.delete(teamFollower);
        return new ApiResponse(Boolean.TRUE, "팀 언팔로우 성공");
    }

}
