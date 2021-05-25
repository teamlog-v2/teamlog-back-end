package com.test.teamlog.payload;

import com.test.teamlog.entity.Task;
import com.test.teamlog.entity.TaskPerformer;
import com.test.teamlog.entity.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDTO {
    @Getter
    public static class TaskRequest {
        private String taskName;
        private TaskStatus status;
        private LocalDateTime deadline;
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
        private List<UserDTO.UserSimpleInfo> performers;
        public TaskResponse(Task task) {
            this.id = task.getId();
            this.taskName = task.getTaskName();
            this.status = task.getStatus().getValue();
            this.updateTime = task.getUpdateTime();
            this.updateTimeStr = task.getUpdateTime().toString();
            this.deadline = task.getDeadline();
            List<UserDTO.UserSimpleInfo> performers = new ArrayList<>();
            if(task.getTaskPerformers() !=null) {
                for (TaskPerformer temp : task.getTaskPerformers()) {
                    UserDTO.UserSimpleInfo userInfo = new UserDTO.UserSimpleInfo(temp.getUser());
                    performers.add(userInfo);
                }
            }
            this.performers = performers;
        }
    }
}