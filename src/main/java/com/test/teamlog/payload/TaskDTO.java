package com.test.teamlog.payload;

import com.test.teamlog.entity.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class TaskDTO {
    @Getter
    public static class TaskRequest {
        private String taskName;
        private TaskStatus status;
        private LocalDateTime deadline;
        private List<String> performersId;
    }

    @Data
    @Builder
    public static class TaskResponse {
        private Long id;
        private String taskName;
        private int status;
        private LocalDateTime deadline;
        private LocalDateTime updateTime;
        private List<UserDTO.UserSimpleInfo> performers;
    }
}