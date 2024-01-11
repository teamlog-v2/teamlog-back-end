package com.test.teamlog.domain.account.service;

import com.test.teamlog.domain.account.dto.*;
import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.repository.AccountRepository;
import com.test.teamlog.domain.file.management.service.FileManagementService;
import com.test.teamlog.domain.token.dto.CreateTokenResult;
import com.test.teamlog.domain.token.service.TokenService;
import com.test.teamlog.domain.userfollow.service.query.UserFollowQueryService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.exception.ResourceAlreadyExistsException;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import com.test.teamlog.global.utility.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    private final TokenService tokenService;
    private final UserFollowQueryService userFollowQueryService;
    private final FileManagementService fileManagementService;

    public List<UserSearchResult> search(String id, String name) {
        List<User> userList = accountRepository.searchUserByIdentificationAndName(id, name);

        return userList.stream().map(UserSearchResult::from).toList();
    }

    public UserValidateResult validate(Long userId) {
        final User currentUser = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER", "id", userId));

        return UserValidateResult.from(currentUser);
    }

    @Transactional(readOnly = true)
    public UserReadDetailResult readDetail(String identification, User currentUser) {
        User user = accountRepository.findByIdentification(identification)
                .orElseThrow(() -> new ResourceNotFoundException("USER", "id", identification));

        UserReadDetailResult result = UserReadDetailResult.from(user);

        if (currentUser == null || !identification.equals(currentUser.getIdentification())) {
            result.setIsMe(currentUser != null ? Boolean.FALSE : null);
            result.setIsFollow(currentUser != null ? userFollowQueryService.isFollow(currentUser, user) : null);
        } else {
            result.setIsMe(Boolean.TRUE);
            result.setIsFollow(Boolean.FALSE);
        }

        return result;
    }

    @Transactional
    public SignInResult signIn(SignInInput input) {
        final String identification = input.getIdentification();
        User user = accountRepository.findByIdentification(identification).orElse(null);

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
        accountRepository.save(user);

        return SignUpResult.from(user);
    }

    @Transactional
    public ApiResponse updateUser(UserUpdateRequest request, MultipartFile image, User currentUser) throws IOException {
        if (request.getDefaultImage()) {
            if (currentUser.getProfileImage() != null) {
                currentUser.setProfileImage(null);
            }
        } else {
            if (image != null) {
                currentUser.updateProfileImage(fileManagementService.uploadFile(image));
            }
        }
        currentUser.setName(request.getName());
        currentUser.setIntroduction(request.getIntroduction());
        accountRepository.save(currentUser);
        return new ApiResponse(Boolean.TRUE, "사용자 정보 수정 성공");
    }

    @Transactional
    public ApiResponse updateUserProfileImage(MultipartFile image, User currentUser) throws IOException {
        currentUser.updateProfileImage(fileManagementService.uploadFile(image));

        accountRepository.save(currentUser);
        return new ApiResponse(Boolean.TRUE, "프로필 이미지 수정 성공");
    }

    @Transactional
    public ApiResponse deleteUserProfileImage(User currentUser) {
        currentUser.setProfileImage(null);

        accountRepository.save(currentUser);
        return new ApiResponse(Boolean.TRUE, "프로필 이미지 삭제 성공");
    }

    //회원 탈퇴
    @Transactional
    public ApiResponse deleteUser(User currentUser) {
        accountRepository.delete(currentUser);
        return new ApiResponse(Boolean.TRUE, "회원 탈퇴 성공");
    }

    public User readByIdentification(String identification) {
        return accountRepository.findByIdentification(identification)
                .orElseThrow(() -> new ResourceNotFoundException("USER", "id", identification));
    }

    public List<User> readAllByIdentificationIn(List<String> identificationList) {
        return accountRepository.findAllByIdentificationIn(identificationList);
    }

    // identification 중복 체크
    private void checkIdDuplication(String identification) {
        if (accountRepository.findByIdentification(identification).isPresent()) {
            throw new ResourceAlreadyExistsException("이미 존재하는 회원입니다.");
        }
    }
}
