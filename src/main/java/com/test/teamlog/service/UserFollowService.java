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
    public List<UserDTO.UserFollowInfo> getFollowerList(String userId, User currentUser){
        User user = userRepository.findById(userId)
                    .orElseThrow(()-> new ResourceNotFoundException("User","ID",userId));
        List<UserFollow> currentUserFollowings = userFollowRepository.findByFromUser(currentUser);

        List<UserFollow> followers = user.getFollowers();
        List<UserDTO.UserFollowInfo> responses = new ArrayList<>();
        for (UserFollow follower : followers) {
            UserDTO.UserFollowInfo temp = new UserDTO.UserFollowInfo(follower.getFromUser());
            for(UserFollow following : currentUserFollowings){
                if(follower.getFromUser().getId().equals(currentUser.getId())) {
                    temp.setIsFollow(null);
                    break;
                }
                if(following.getToUser().equals(follower.getFromUser())) {
                    temp.setIsFollow(Boolean.TRUE);
                    break;
                } else{
                    temp.setIsFollow(Boolean.FALSE);
                }
            }
            responses.add(temp);
        }
        return responses;
    }

    // 팔로잉 리스트 조회
    public List<UserDTO.UserFollowInfo> getFollowingList(String userId, User currentUser){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User","ID",userId));
        List<UserFollow> currentUserFollowings = userFollowRepository.findByFromUser(currentUser);
        List<UserFollow> followings = user.getFollowing();
        List<UserDTO.UserFollowInfo> responses = new ArrayList<>();
        for (UserFollow following : followings) {
            UserDTO.UserFollowInfo temp = new UserDTO.UserFollowInfo(following.getToUser());
            for(UserFollow currentUserfollowing : currentUserFollowings){
                if(following.getToUser().getId().equals(currentUser.getId())) {
                    temp.setIsFollow(null);
                    break;
                }
                if(currentUserfollowing.getToUser().equals(following.getToUser())) {
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

    // 팔로우
    @Transactional
    public ApiResponse followUser(User currentUser, String targetUserID){
        User targetUser = userRepository.findById(targetUserID)
                .orElseThrow(()-> new ResourceNotFoundException("User","ID",targetUserID));

        UserFollow newFollow = UserFollow.builder()
                .fromUser(currentUser)
                .toUser(targetUser)
                .build();

        userFollowRepository.save(newFollow);
        return new ApiResponse(Boolean.TRUE, "팔로우 성공");
    }

    // 언팔로우
    @Transactional
    public ApiResponse unfollowUser(String targetUserId, User currentUser) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(()-> new ResourceNotFoundException("User","ID",targetUserId));
        UserFollow userFollow = userFollowRepository.findByFromUserAndToUser(currentUser,targetUser)
                .orElseThrow(() -> new ResourceNotFoundException("UserFollow", "FromUserId", currentUser.getId()));
        userFollowRepository.delete(userFollow);
        return new ApiResponse(Boolean.TRUE, "언팔로우 성공");
    }

    public Boolean isFollow(User fromUser, User targetUser) {
        return userFollowRepository.findByFromUserAndToUser(fromUser, targetUser).isPresent();
    }

}
