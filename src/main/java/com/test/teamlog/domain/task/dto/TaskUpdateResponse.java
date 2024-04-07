package com.test.teamlog.domain.task.dto;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskUpdateResponse {
    private Long id;
    private String taskName;
    private int status;
    private LocalDateTime deadline;
    private LocalDateTime updateTime;
    private String updateTimeStr;
    private List<TaskPerformerResponse> performers;

    public static TaskUpdateResponse from(TaskUpdateResult result) {
        TaskUpdateResponse response = new TaskUpdateResponse();
        response.setId(result.getTaskId());
        response.setTaskName(result.getTaskName());
        response.setStatus(result.getStatus());
        response.setDeadline(result.getDeadline());
        response.setUpdateTime(result.getUpdateTime());
        response.setUpdateTimeStr(result.getUpdateTime().toString());
        response.setPerformers(
                !CollectionUtils.isEmpty(result.getPerformers()) ?
                        result.getPerformers().stream().map(TaskPerformerResponse::from).toList() :
                        new ArrayList<>()
        );

        return response;
    }

    @Data
    static class TaskPerformerResponse {
        private String id;
        private String name;
        private String profileImgPath;

        public static TaskPerformerResponse from(TaskUpdateResult.TaskPerformerResult result) {
            final TaskPerformerResponse response = new TaskPerformerResponse();
            response.setId(result.getId());
            response.setName(result.getName());
            response.setProfileImgPath(result.getProfileImgPath());

            return response;
        }
    }
}
