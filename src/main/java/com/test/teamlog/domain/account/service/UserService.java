package com.test.teamlog.domain.account.service;

import com.test.teamlog.domain.account.dto.*;
import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.repository.UserRepository;
import com.test.teamlog.domain.token.dto.CreateTokenResult;
import com.test.teamlog.domain.token.service.TokenService;
import com.test.teamlog.entity.ProjectMember;
import com.test.teamlog.entity.TeamMember;
import com.test.teamlog.exception.BadRequestException;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.global.utility.PasswordUtil;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.repository.ProjectMemberRepository;
import com.test.teamlog.repository.TeamMemberRepository;
import com.test.teamlog.service.FileStorageService;
import com.test.teamlog.service.UserFollowService;
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
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final UserFollowService userFollowService;
    private final ProjectMemberRepository projectMemberRepository;
    private final TeamMemberRepository teamMemberRepository;

    public List<UserRequest.UserSimpleInfo> searchUser(String id, String name) {
        List<User> userList = userRepository.searchUserByIdentificationAndName(id, name);
        List<UserRequest.UserSimpleInfo> response = new ArrayList<>();
        for (User user : userList) {
            response.add(new UserRequest.UserSimpleInfo(user));
        }
        return response;
    }

    public UserRequest.UserResponse getUser(String identification, User currentUser) {
        UserRequest.UserResponse response = null;
        if (currentUser == null || !identification.equals(currentUser.getIdentification())) {
            User user = userRepository.findByIdentification(identification)
                    .orElseThrow(() -> new ResourceNotFoundException("USER", "id", identification));
            response = new UserRequest.UserResponse(user);
            response.setIsMe(Boolean.FALSE);
            if (currentUser == null) response.setIsMe(null);
            response.setIsFollow(userFollowService.isFollow(currentUser, user));
        } else {
            response = new UserRequest.UserResponse(currentUser);
            response.setIsMe(Boolean.TRUE);
            response.setIsFollow(Boolean.FALSE);
        }
        return response;
    }

    @Transactional
    public SignInResult signIn(SignInInput input) {
        final String identification = input.getIdentification();
        User user = userRepository.findByIdentification(identification).orElse(null);

        // FIXME: 추후 Exception 바꿀 예정. 프론트와 같이 바꿔야 한다.
        if (user == null || !PasswordUtil.matches(input.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("USER", "IDENTIFICATION", identification);
        }

        final CreateTokenResult createTokenResult = tokenService.createToken(identification);
        return SignInResult.from(createTokenResult);
    }

    // 회원 가입
    @Transactional
    public SignUpResult signUp(SignUpInput input) {
        checkIdDuplication(input.getIdentification());

        final User user = input.toUser();
        userRepository.save(user);

        return SignUpResult.from(user);
    }

    // identification 중복 체크
    private void checkIdDuplication(String identification) {
        if (userRepository.findByIdentification(identification).isPresent()) {
            throw new ResourceAlreadyExistsException("이미 존재하는 회원입니다.");
        }
    }

    @Transactional
    public ApiResponse updateUser(UserRequest.UserUpdateRequest userRequest, MultipartFile image, User currentUser) {
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
        if (teamMemberList.size() != 0) {
            throw new BadRequestException("가입된 팀이 있습니다.\n모든 팀 탈퇴 후 진행해주세요.");
        }
        List<ProjectMember> projectMemberList = projectMemberRepository.findByUser(currentUser);
        if (projectMemberList.size() != 0) {
            throw new BadRequestException("가입된 프로젝트가 있습니다.\n모든 프로젝트 탈퇴 후 진행해주세요.");
        }

        userRepository.delete(currentUser);
        return new ApiResponse(Boolean.TRUE, "회원 탈퇴 성공");
    }
}
