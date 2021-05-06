package com.test.teamlog.service;

import com.test.teamlog.entity.User;
import com.test.teamlog.entity.UserFollow;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.repository.UserFollowRepository;
import com.test.teamlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFollowService {
    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    // 팔로워 리스트 조회
    public List<UserDTO.UserFollowInfo> getFollowerList(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User","ID",userId));
        List<UserFollow> followers = user.getFollowers();
        List<UserDTO.UserFollowInfo> responses = new ArrayList<>();
        for (UserFollow follower : followers) {
            UserDTO.UserFollowInfo temp = new UserDTO.UserFollowInfo(follower.getFromUser());
            for(UserFollow following : user.getFollowing()){
                if(following.getToUser().equals(follower.getFromUser())) {
                    temp.setIsFollow(Boolean.TRUE);
                    break;
                } else {
                    temp.setIsFollow(Boolean.FALSE);
                }
            }
            responses.add(temp);
        }
        return responses;
    }

    // 팔로잉 리스트 조회
    public List<UserDTO.UserFollowInfo> getFollowingList(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User","ID",userId));
        List<UserFollow> followings = user.getFollowing();
        List<UserDTO.UserFollowInfo> responses = new ArrayList<>();
        for (UserFollow following : followings) {
            UserDTO.UserFollowInfo temp = new UserDTO.UserFollowInfo(following.getToUser());
            temp.setIsFollow(Boolean.TRUE);
            responses.add(temp);
        }
        return responses;
    }

    // 팔로우
    @Transactional
    public ApiResponse followUser(String userId, String targetUserID){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User","ID",userId));
        User targetUser = userRepository.findById(targetUserID)
                .orElseThrow(()-> new ResourceNotFoundException("User","ID",targetUserID));

        UserFollow newFollow = UserFollow.builder()
                .fromUser(user)
                .toUser(targetUser)
                .build();

        userFollowRepository.save(newFollow);
        return new ApiResponse(Boolean.TRUE, "팔로우 성공");
    }

    // 언팔로우
    @Transactional
    public ApiResponse unfollowUser(String userId, String targetUserID) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User","ID",userId));
        User targetUser = userRepository.findById(targetUserID)
                .orElseThrow(()-> new ResourceNotFoundException("User","ID",targetUserID));
        UserFollow userFollow = userFollowRepository.findByFromUserAndToUser(user,targetUser);
        userFollowRepository.delete(userFollow);
        return new ApiResponse(Boolean.TRUE, "언팔로우 성공");
    }

}
