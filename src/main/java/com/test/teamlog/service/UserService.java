package com.test.teamlog.service;

import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.ProjectMember;
import com.test.teamlog.entity.TeamMember;
import com.test.teamlog.entity.User;
import com.test.teamlog.exception.BadRequestException;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.repository.ProjectMemberRepository;
import com.test.teamlog.repository.TeamMemberRepository;
import com.test.teamlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final UserFollowService userFollowService;
    private final ProjectMemberRepository projectMemberRepository;
    private final TeamMemberRepository teamMemberRepository;

    public List<UserDTO.UserSimpleInfo> searchUser(String id, String name) {
        List<User> userList = userRepository.searchUserByIdAndName(id,name);
        List<UserDTO.UserSimpleInfo> response = new ArrayList<>();
        for(User user : userList) {
            response.add(new UserDTO.UserSimpleInfo(user));
        }
        return response;
    }

    public UserDTO.UserResponse getUser(String id, User currentUser) {
        UserDTO.UserResponse response = null;
        if(currentUser == null || !id.equals(currentUser.getId())) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("USER", "id", id));
            response = new UserDTO.UserResponse(user);
            response.setIsMe(Boolean.FALSE);
            if(currentUser==null) response.setIsMe(null);
            response.setIsFollow(userFollowService.isFollow(currentUser, user));
        } else {
            response = new UserDTO.UserResponse(currentUser);
            response.setIsMe(Boolean.TRUE);
            response.setIsFollow(Boolean.FALSE);
        }
        return response;
    }

    // 로그인
    public User signIn(UserDTO.SignInRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("USER", "ID", request.getId()));
        if (request.getPassword().equals(user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

    // 회원 가입
    @Transactional
    public UserDTO.UserSimpleInfo signUp(UserDTO.UserRequest userRequest) {
        validateDuplicateuId(userRequest.getId());
        User user = User.builder()
                .id(userRequest.getId())
                .password(userRequest.getPassword())
                .name(userRequest.getName())
                .introduction(userRequest.getIntroduction())
                .profileImgPath(userRequest.getProfileImgPath())
                .build();
        userRepository.save(user);
        return new UserDTO.UserSimpleInfo(user);
    }

    // id 중복 체크
    private void validateDuplicateuId(String id) {
        if (userRepository.findById(id).isPresent()) {
            throw new ResourceAlreadyExistsException("이미 존재하는 회원입니다.");
        }
    }

    @Transactional
    public ApiResponse updateUser(UserDTO.UserUpdateRequest userRequest, MultipartFile image, User currentUser) {
        if (userRequest.getDefaultImage()) {
            if (currentUser.getProfileImgPath() != null) {
                fileStorageService.deleteFile(currentUser.getProfileImgPath());
                currentUser.setProfileImgPath(null);
            }
        } else {
            if (image != null) {
                if (currentUser.getProfileImgPath() != null) {
                    fileStorageService.deleteFile(currentUser.getProfileImgPath());
                    currentUser.setProfileImgPath(null);
                }
                String profileImgPath = fileStorageService.storeFile(image, null, null);
                currentUser.setProfileImgPath(profileImgPath);
            }
        }
        currentUser.setName(userRequest.getName());
        currentUser.setIntroduction(userRequest.getIntroduction());
        userRepository.save(currentUser);
        return new ApiResponse(Boolean.TRUE, "사용자 정보 수정 성공");
    }

    @Transactional
    public ApiResponse updateUserProfileImage(MultipartFile image, User currentUser) {
        if (currentUser.getProfileImgPath() != null) {
            fileStorageService.deleteFile(currentUser.getProfileImgPath());
            currentUser.setProfileImgPath(null);
        }
        String profileImgPath = fileStorageService.storeFile(image, null, null);
        currentUser.setProfileImgPath(profileImgPath);
        userRepository.save(currentUser);
        return new ApiResponse(Boolean.TRUE, "프로필 이미지 수정 성공");
    }

    @Transactional
    public ApiResponse deleteUserProfileImage(User currentUser) {
        if (currentUser.getProfileImgPath() != null) {
            fileStorageService.deleteFile(currentUser.getProfileImgPath());
            currentUser.setProfileImgPath(null);
        }
        userRepository.save(currentUser);
        return new ApiResponse(Boolean.TRUE, "프로필 이미지 삭제 성공");
    }

    //회원 탈퇴
    @Transactional
    public ApiResponse deleteUser(User currentUser) {
        List<TeamMember> teamMemberList = teamMemberRepository.findByUser(currentUser);
        if(teamMemberList.size() != 0) {
            throw new BadRequestException("가입된 팀이 있습니다.\n모든 팀 탈퇴 후 진행해주세요.");
        }
        List<ProjectMember> projectMemberList = projectMemberRepository.findByUser(currentUser);
        if(projectMemberList.size() != 0) {
            throw new BadRequestException("가입된 프로젝트가 있습니다.\n모든 프로젝트 탈퇴 후 진행해주세요.");
        }
        userRepository.delete(currentUser);
        return new ApiResponse(Boolean.TRUE, "회원 탈퇴 성공");
    }
}
