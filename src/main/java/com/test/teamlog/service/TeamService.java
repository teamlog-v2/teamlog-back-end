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

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamJoinRepository teamJoinRepository;

    // 팀 멤버 아닌 유저 리스트
    public List<UserDTO.UserSimpleInfo> getUsersNotInTeamMember(Long teamId) {
        List<User> userList = userRepository.getUsersNotInTeamMember(teamId);
        List<UserDTO.UserSimpleInfo> response = new ArrayList<>();
        for(User user : userList) {
            response.add(new UserDTO.UserSimpleInfo(user));
        }
        return response;
    }

    // 팀 조회
    public TeamDTO.TeamResponse getTeam(Long id, User currentUser) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
        // Private 시 검증
        if(team.getAccessModifier() == AccessModifier.PRIVATE) {
            validateUserIsMemberOfTeam(team, currentUser);
        }
        TeamDTO.TeamResponse response = new TeamDTO.TeamResponse(team);
        return response;
    }

    // 사용자 팀 리스트 조회
    public List<TeamDTO.TeamListResponse> getTeamsByUser(String id, User currentUser) {
        User user = null;
        boolean isMyTeamList = currentUser.getId().equals(id);
        if(isMyTeamList){
            user = currentUser;
        } else {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("USER", "id", id));
        }
        List<TeamMember> teams = teamMemberRepository.findByUser(user);

        List<TeamDTO.TeamListResponse> teamList = new ArrayList<>();
        for (TeamMember temp : teams) {
            Team team = temp.getTeam();
            if(!isMyTeamList) {
                // 팀 멤버도 아니고 private면 x
                if(!isUserMemberOfTeam(team,currentUser) && team.getAccessModifier() == AccessModifier.PRIVATE) continue;
            }

            TeamDTO.TeamListResponse item = TeamDTO.TeamListResponse.builder()
                    .id(team.getId())
                    .name(team.getName())
                    .updateTime(team.getUpdateTime())
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

    // 팀 마스터 위임
    @Transactional
    public ApiResponse delegateTeamMaster(Long id, String newMasterId, User currentUser) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
        validateUserIsMaster(team, currentUser);

        User newMaster = userRepository.findById(newMasterId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", newMasterId));
        team.setMaster(newMaster);
        teamRepository.save(team);
        return new ApiResponse(Boolean.TRUE, "팀 마스터 위임 성공");
    }

    // 팀 삭제
    @Transactional
    public ApiResponse deleteTeam(Long id, User currentUser) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
        validateUserIsMaster(team, currentUser);

        // TODO : 팀 삭제시 팀에서 만들어진 프로젝트는 우짤건지.

        teamRepository.delete(team);
        return new ApiResponse(Boolean.TRUE, "팀 삭제 성공");
    }


    // -------------------------------
    // ------- 팀 멤버 신청 관리 -------
    // -------------------------------
    // 팀 멤버 초대
    @Transactional
    public ApiResponse inviteUserForTeam(Long teamId, String userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (isUserMemberOfTeam(team, user))
            throw new ResourceAlreadyExistsException("이미 해당 팀의 멤버입니다.");

        if (isJoinAlreadyExist(team, user))
            throw new ResourceAlreadyExistsException("이미 해당 팀에 멤버 신청 혹은 초대가 존재합니다.");

        TeamJoin teamJoin = TeamJoin.builder()
                .team(team)
                .user(user)
                .isInvited(Boolean.TRUE)
                .build();
        teamJoinRepository.save(teamJoin);

        return new ApiResponse(Boolean.TRUE, "유저 : " + user.getName() + " 초대 완료");
    }

    // 팀 멤버 신청
    @Transactional
    public ApiResponse applyForTeam(Long teamId, User currentUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));

        if (isUserMemberOfTeam(team, currentUser))
            throw new ResourceAlreadyExistsException("이미 해당 팀의 멤버입니다.");

        if (isJoinAlreadyExist(team, currentUser))
            throw new ResourceAlreadyExistsException("이미 해당 팀에 멤버 신청 혹은 초대가 존재합니다.");

        TeamJoin teamJoin = TeamJoin.builder()
                .team(team)
                .user(currentUser)
                .isAccepted(Boolean.TRUE)
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
                    .teamName(join.getTeam().getName())
                    .build();
            response.add(temp);
        }
        return response;
    }

    // ---------------------------
    // ----- 팀 멤버 관리 -----
    // ---------------------------
    // 팀 멤버 추가
    @Transactional
    public ApiResponse acceptTeamInvitation(Long id) {
        TeamJoin join = teamJoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeamInvitation", "ID", id));
        // TODO : join 삭제 할지 말지 ( 프로젝트도 ... )
        join.setAccepted(Boolean.TRUE);
        join.setInvited(Boolean.TRUE);

        teamJoinRepository.save(join);

        TeamMember newMember = TeamMember.builder()
                .team(join.getTeam())
                .user(join.getUser())
                .build();
        teamMemberRepository.save(newMember);
        return new ApiResponse(Boolean.TRUE, "팀 멤버 추가");
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
        validateUserIsMaster(team, currentUser);
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

    // ---------------------------
    // -------- 검증 메소드 --------
    // ---------------------------
    // 마스터 검증
    private void validateUserIsMaster(Team team, User currentUser) {
        if (team.getMaster().getId() != currentUser.getId())
            throw new ResourceForbiddenException("권한이 없습니다.( 팀 마스터 아님 )");
    }

    //
    // 이미 TeamJoin 있을 경우
    public Boolean isJoinAlreadyExist(Team team, User currentUser) {
        return teamJoinRepository.findByTeamAndUser(team, currentUser).isPresent();
    }

    // 팀 멤버인지 아닌지
    public Boolean isUserMemberOfTeam(Team team, User currentUser) {
        return teamMemberRepository.findByTeamAndUser(team, currentUser).isPresent();
    }

    // 팀 멤버 검증
    public void validateUserIsMemberOfTeam(Team team, User currentUser) {
        teamMemberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new ResourceForbiddenException("권한이 없습니다. ( 팀 멤버 아님 )"));
    }

}
