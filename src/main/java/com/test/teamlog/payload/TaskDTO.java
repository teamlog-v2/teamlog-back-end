package com.test.teamlog.payload;

import com.test.teamlog.domain.account.dto.UserRequest;

import com.test.teamlog.entity.Task;
import com.test.teamlog.entity.TaskPerformer;
import com.test.teamlog.entity.TaskStatus;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDTO {
    @Getter
    public static class TaskRequest {
        @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
        @Size(min=1,max=30, message = "태스크 이름을 1자에서 30자 사이로 입력해주세요.")
        private String taskName;
        private TaskStatus status;
        private ZonedDateTime deadline;
        private List<String> performersId;
    }

    @Getter
    public static class TaskDropLocation {
        private TaskStatus status;
        private Integer priority;
    }

    @Data
    public static class TaskResponse {
        private Long id;
        private String taskName;
        private int status;
        private LocalDateTime deadline;
        private LocalDateTime updateTime;
        private String updateTimeStr;
        private List<UserRequest.UserSimpleInfo> performers;
        public TaskResponse(Task task) {
            this.id = task.getId();
            this.taskName = task.getTaskName();
            this.status = task.getStatus().getValue();
            this.updateTime = task.getUpdateTime();
            this.updateTimeStr = task.getUpdateTime().toString();
            this.deadline = task.getDeadline();
            List<UserRequest.UserSimpleInfo> performers = new ArrayList<>();
            if(task.getTaskPerformers() !=null) {
                for (TaskPerformer temp : task.getTaskPerformers()) {
                    UserRequest.UserSimpleInfo userInfo = new UserRequest.UserSimpleInfo(temp.getUser());
                    performers.add(userInfo);
                }
            }
            this.performers = performers;
        }
    }
}