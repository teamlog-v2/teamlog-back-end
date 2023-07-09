package com.test.teamlog.payload;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.entity.PostUpdateHistory;
import lombok.Getter;

import java.time.LocalDateTime;

public class PostDTO {
    @Getter
    public static class PostHistoryInfo {
        private UserRequest.UserSimpleInfo writer;
        private LocalDateTime writeTime;
        public PostHistoryInfo(PostUpdateHistory history) {
            this.writer = new UserRequest.UserSimpleInfo(history.getUser());
            this.writeTime = history.getCreateTime();
        }
    }
}
