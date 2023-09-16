package com.test.teamlog.domain.userfollow.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.service.AccountService;
import com.test.teamlog.domain.userfollow.dto.UserFollowerReadResult;
import com.test.teamlog.domain.userfollow.dto.UserFollowingReadResult;
import com.test.teamlog.domain.userfollow.entity.UserFollow;
import com.test.teamlog.domain.userfollow.repository.UserFollowRepository;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFollowService {
    private final UserFollowRepository userFollowRepository;

    private final AccountService accountService;

    // 팔로워 리스트 조회
    public List<UserFollowerReadResult> readAllFollower(String userId, User currentUser) {
        final User user = accountService.readByIdentification(userId);

        List<UserFollow> followingList = userFollowRepository.findAllByFromUser(currentUser); // 유저가 팔로우하는 사람들 (내가 from)
        List<UserFollow> followerList = userFollowRepository.findAllByToUser(user); // 유저를 팔로우하는 사람들 (내가 to)

        List<UserFollowerReadResult> resultList = new ArrayList<>();

        for (UserFollow follower : followerList) {
            UserFollowerReadResult result = UserFollowerReadResult.from(follower);

            if (currentUser == null || follower.getFromUser().getIdentification().equals(currentUser.getIdentification())) {
                result.setIsFollow(null);
                resultList.add(result);
                continue;
            }

            result.setIsFollow(Boolean.FALSE);
            for (UserFollow following : followingList) {
                if (following.getToUser().equals(follower.getFromUser())) {
                    result.setIsFollow(Boolean.TRUE);
                    break;
                }
            }

            resultList.add(result);
        }

        return resultList;
    }

    // 팔로잉 리스트 조회
    public List<UserFollowingReadResult> readAllFollowing(String userId, User currentUser) {
        User user = accountService.readByIdentification(userId);

        List<UserFollow> currentUserFollowingList = userFollowRepository.findAllByFromUser(currentUser); // 로그인한 사람의 팔로잉 목록
        List<UserFollow> followingList = user.getFollowings(); // 특정 유저의 팔로잉 목록

        List<UserFollowingReadResult> resultList = new ArrayList<>();

        for (UserFollow following : followingList) {
            final UserFollowingReadResult result = UserFollowingReadResult.from(following);

            // 특정 유저가 팔로우한 사람이 본인일 경우 팔로우 표시를 하지 않는다.
            if (currentUser == null || following.getToUser().getIdentification().equals(currentUser.getIdentification())) {
                result.setIsFollow(null);
                resultList.add(result);
                continue;
            }

            // 특정 사람이 팔로잉한 사람 중 나도 팔로잉 중이면 isFollow = true -> 프론트에서 팔로잉이라고 표시한다.
            result.setIsFollow(Boolean.FALSE);
            for (UserFollow currentUserFollowing : currentUserFollowingList) {
                if (currentUserFollowing.getToUser().equals(following.getToUser())) {
                    result.setIsFollow(Boolean.TRUE);
                    break;
                }
            }

            resultList.add(result);
        }

        return resultList;
    }

    // 팔로우
    @Transactional
    public ApiResponse follow(User currentUser, String targetUserId) {
        User targetUser = accountService.readByIdentification(targetUserId);

        userFollowRepository.save(UserFollow.create(currentUser, targetUser));
        return new ApiResponse(Boolean.TRUE, "팔로우 성공");
    }

    // 언팔로우
    @Transactional
    public ApiResponse unfollow(String targetUserId, User currentUser) {
        User targetUser = accountService.readByIdentification(targetUserId);
        UserFollow userFollow = userFollowRepository.findByFromUserAndToUser(currentUser, targetUser)
                .orElseThrow(() -> new ResourceNotFoundException("UserFollow", "FromUserId", currentUser.getIdentification()));

        userFollowRepository.delete(userFollow);
        return new ApiResponse(Boolean.TRUE, "언팔로우 성공");
    }

    public Boolean isFollow(User fromUser, User targetUser) {
        return userFollowRepository.findByFromUserAndToUser(fromUser, targetUser).isPresent();
    }

    public List<UserFollow> readAllByUser(User currentUser) {
        return userFollowRepository.findAllByFromUser(currentUser);
    }
}
