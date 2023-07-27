package com.test.teamlog.service;

import com.test.teamlog.domain.account.model.User;

import com.test.teamlog.domain.account.repository.AccountRepository;
import com.test.teamlog.entity.UserFollow;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.repository.UserFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFollowService {
    private final AccountRepository accountRepository;
    private final UserFollowRepository userFollowRepository;

    // 팔로워 리스트 조회
    public List<UserRequest.UserFollowInfo> getFollowerList(String userId, User currentUser) {
        User user = accountRepository.findByIdentification(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
        List<UserFollow> currentUserFollowings = userFollowRepository.findByFromUser(currentUser);
        List<UserFollow> followers = user.getFollowers();
        List<UserRequest.UserFollowInfo> responses = new ArrayList<>();
        for (UserFollow follower : followers) {
            UserRequest.UserFollowInfo temp = new UserRequest.UserFollowInfo(follower.getFromUser());
            if (currentUser==null || follower.getFromUser().getIdentification().equals(currentUser.getIdentification())) {
                temp.setIsFollow(null);
                responses.add(temp);
                continue;
            }
            temp.setIsFollow(Boolean.FALSE);
            if (currentUserFollowings.size() > 0) {
                for (UserFollow following : currentUserFollowings) {
                    if (following.getToUser().equals(follower.getFromUser())) {
                        temp.setIsFollow(Boolean.TRUE);
                        break;
                    }
                }
            }
            responses.add(temp);
        }
        return responses;
    }

    // 팔로잉 리스트 조회
    public List<UserRequest.UserFollowInfo> getFollowingList(String userId, User currentUser) {
        User user = accountRepository.findByIdentification(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
        List<UserFollow> currentUserFollowings = userFollowRepository.findByFromUser(currentUser);
        List<UserFollow> followings = user.getFollowing();
        List<UserRequest.UserFollowInfo> responses = new ArrayList<>();
        for (UserFollow following : followings) {
            UserRequest.UserFollowInfo temp = new UserRequest.UserFollowInfo(following.getToUser());
            if (currentUser==null || following.getToUser().getIdentification().equals(currentUser.getIdentification())) {
                temp.setIsFollow(null);
                responses.add(temp);
                continue;
            }
            temp.setIsFollow(Boolean.FALSE);
            if (currentUserFollowings.size() > 0) {
                for (UserFollow currentUserfollowing : currentUserFollowings) {
                    if (currentUserfollowing.getToUser().equals(following.getToUser())) {
                        temp.setIsFollow(Boolean.TRUE);
                        break;
                    }
                }
            }
            responses.add(temp);
        }
        return responses;
    }

    // 팔로우
    @Transactional
    public ApiResponse followUser(User currentUser, String targetUserID) {
        User targetUser = accountRepository.findByIdentification(targetUserID)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", targetUserID));

        UserFollow newFollow = UserFollow.builder()
                .fromUser(currentUser)
                .toUser(targetUser)
                .build();
        try {
            userFollowRepository.saveAndFlush(newFollow);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("이미 팔로우 중 입니다.");
        }
        return new ApiResponse(Boolean.TRUE, "팔로우 성공");
    }

    // 언팔로우
    @Transactional
    public ApiResponse unfollowUser(String targetUserId, User currentUser) {
        User targetUser = accountRepository.findByIdentification(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", targetUserId));
        UserFollow userFollow = userFollowRepository.findByFromUserAndToUser(currentUser, targetUser)
                .orElseThrow(() -> new ResourceNotFoundException("UserFollow", "FromUserId", currentUser.getIdentification()));
        userFollowRepository.delete(userFollow);
        return new ApiResponse(Boolean.TRUE, "언팔로우 성공");
    }

    public Boolean isFollow(User fromUser, User targetUser) {
        return userFollowRepository.findByFromUserAndToUser(fromUser, targetUser).isPresent();
    }

    public List<UserFollow> readUserFollowList(User currentUser) {
        return userFollowRepository.findByFromUser(currentUser);
    }
}
