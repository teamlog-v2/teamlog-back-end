package com.app.teamlog.domain.task.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.domain.task.entity.Task;
import com.app.teamlog.domain.task.entity.TaskPerformer;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskUpdateResult {
    private Long taskId;
    private String taskName;
    private int status;
    private LocalDateTime deadline;
    private LocalDateTime updateTime;
    private String updateTimeStr;
    private List<TaskPerformerResult> performers;

    public static TaskUpdateResult from(Task task) {
        TaskUpdateResult response = new TaskUpdateResult();
        response.setTaskId(task.getId());
        response.setTaskName(task.getTaskName());
        if (task.getStatus() != null) response.setStatus(task.getStatus().getValue());
        response.setDeadline(task.getDeadline());
        response.setUpdateTime(task.getUpdateTime());
        response.setPerformers(
                !CollectionUtils.isEmpty(task.getTaskPerformers()) ?
                        task.getTaskPerformers().stream().map(TaskPerformerResult::from).toList() :
                        new ArrayList<>()
        );

        return response;
    }

    @Data
    public static class TaskPerformerResult {
        private String id;
        private String name;
        private String profileImgPath;

        public static TaskPerformerResult from(TaskPerformer performer) {
            final Account account = performer.getAccount();

            final TaskPerformerResult result = new TaskPerformerResult();
            result.setId(account.getIdentification());
            result.setName(account.getName());

            final FileInfo profileImage = account.getProfileImage();
            if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

            return result;
        }
    }
}
