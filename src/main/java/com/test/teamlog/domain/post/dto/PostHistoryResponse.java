package com.test.teamlog.domain.post.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostHistoryResponse {
    private UserSimpleInfoResponse writer;
    private LocalDateTime writeTime;

    public static PostHistoryResponse from(PostUpdateHistory history) {
        final PostHistoryResponse response = new PostHistoryResponse();
        response.setWriter(UserSimpleInfoResponse.from(history.getUser()));
        response.setWriteTime(history.getCreateTime());

        return response;
    }

    @Data
    static class UserSimpleInfoResponse {
        private String id;
        private String name;
        private String profileImgPath;

        public static UserSimpleInfoResponse from(User user) {
            UserSimpleInfoResponse userFollowInfo = new UserSimpleInfoResponse();
            userFollowInfo.setId(user.getIdentification());
            userFollowInfo.setName(user.getName());

            if (user.getProfileImage() != null) {
                userFollowInfo.setProfileImgPath(user.getProfileImage().getStoredFilePath());
            }

            return userFollowInfo;
        }
    }
}