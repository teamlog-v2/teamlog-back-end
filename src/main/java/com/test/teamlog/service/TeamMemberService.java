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
public class TeamMemberService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamJoinRepository teamJoinRepository;
    private final TeamService teamService;

    // 팀 멤버 아닌 유저 리스트
    public List<UserDTO.UserSimpleInfo> getUsersNotInTeamMember(Long teamId) {
        List<User> userList = userRepository.getUsersNotInTeamMember(teamId);
        List<UserDTO.UserSimpleInfo> response = new ArrayList<>();
        for(User user : userList) {
            response.add(new UserDTO.UserSimpleInfo(user));
        }
        return response;
    }

    // ---------------------------
    // ----- 팀 멤버 관리 -----
    // ---------------------------
    // 팀 멤버 추가 (초대 수락)
    @Transactional
    public ApiResponse createTeamMember(Long id, User currentUser) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        TeamJoin join = teamJoinRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("TeamInvitation", "ID", id));
        if(join.getIsInvited() != true || join.getIsAccepted() != false) throw new BadRequestException("잘못된 요청입니다.");
        teamJoinRepository.delete(join);

        TeamMember newMember = TeamMember.builder()
                .team(join.getTeam())
                .user(join.getUser())
                .build();
        teamMemberRepository.save(newMember);
        return new ApiResponse(Boolean.TRUE, "팀 멤버 가입 됨");
    }

    // 팀 멤버 추가
    @Transactional
    public ApiResponse acceptTeamInvitation(Long id) {
        TeamJoin join = teamJoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectInvitation", "ID", id));
        // TODO : join 삭제 할지 말지?
        // TODO : 수락하는 사람이 마스터이냐 사용자이냐에 따라 구분해야함.
        teamJoinRepository.delete(join);

        TeamMember newMember = TeamMember.builder()
                .team(join.getTeam())
                .user(join.getUser())
                .build();
        teamMemberRepository.save(newMember);
        return new ApiResponse(Boolean.TRUE, "팀 멤버 추가 됨");
    }


    // 팀 멤버 조회
    public List<UserDTO.UserSimpleInfo> getTeamMemberList(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
        List<TeamMember> members = teamMemberRepository.findByTeam(team);

        List<UserDTO.UserSimpleInfo> memberList = new ArrayList<>();
        for (TeamMember member : members) {
            memberList.add(new UserDTO.UserSimpleInfo(member.getUser()));
        }

        return memberList;
    }

    // 팀 나가기
    @Transactional
    public ApiResponse leaveTeam(Long teamId, User currentUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        // TODO : 자기자신이 마스터면 나갈 수 없어야함.
        TeamMember member = teamMemberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("TeamMemeber", "UserId", currentUser.getId()));
        teamMemberRepository.delete(member);
        return new ApiResponse(Boolean.TRUE, "팀 탈퇴 완료");
    }

    // 마스터 - 팀 멤버 삭제
    @Transactional
    public ApiResponse expelMember(Long teamId, String userId, User currentUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        teamService.validateUserIsMaster(team, currentUser);
        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new ResourceNotFoundException("TeamMemeber", "UserId", userId));
        teamMemberRepository.delete(member);
        return new ApiResponse(Boolean.TRUE, "팀 멤버 삭제 완료");
    }

    // member pk 까지 준다면 (마스터)
    @Transactional
    public ApiResponse deleteTeamMemeber(Long id) {
        TeamMember member = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeamMemeber", "id", id));
        teamMemberRepository.delete(member);
        return new ApiResponse(Boolean.TRUE, "팀 멤버 삭제 완료");
    }

}
