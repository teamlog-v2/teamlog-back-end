package com.test.teamlog.service;

import com.test.teamlog.entity.User;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.common.ApiResponse;
import com.test.teamlog.payload.user.UserRequest;
import com.test.teamlog.payload.user.UserResponse;
import com.test.teamlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //회원 조회
    public UserResponse getUser(String id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("USER","id",id));
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .introduction(user.getIntroduction())
                .profileImgPath(user.getProfileImgPath())
                .build();
        return userResponse;
    }

    // 회원 가입
    @Transactional
    public void signUp(UserRequest userRequest){
        validateDuplicateuId(userRequest.getId());
        User user = User.builder()
                .id(userRequest.getId())
                .password(userRequest.getPassword())
                .name(userRequest.getName())
                .introduction(userRequest.getIntroduction())
                .profileImgPath(userRequest.getProfileImgPath())
                .build();
        userRepository.save(user);
    }

    // id 중복 체크
    private void validateDuplicateuId(String id) {
        if(userRepository.findById(id).isPresent()){
            // TODO : 다른 exception 만들어서 처리
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 수정 dto를 더 만들어야할까...
    @Transactional
    public void updateUser(String id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("USER","ID",id));
        user.setName(userRequest.getName());
        user.setIntroduction(userRequest.getIntroduction());
        user.setProfileImgPath(user.getProfileImgPath());
        userRepository.save(user);
    }

    //회원 탈퇴
    @Transactional
    public ApiResponse deleteUser(String id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("USER","ID",id));
        // TODO : 허가된 사용자인지 검증해야함..
        userRepository.delete(user);
        return new ApiResponse(Boolean.TRUE,"회원 탈퇴 성공");
    }
}
