package com.test.teamlog.payload;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class PostDTO {
    @Getter
    public static class PostHistoryInfo {
        private UserSimpleInfoResponse writer;
        private LocalDateTime writeTime;

        public PostHistoryInfo(PostUpdateHistory history) {
            this.writer = UserSimpleInfoResponse.from(history.getUser());
            this.writeTime = history.getCreateTime();
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
}
