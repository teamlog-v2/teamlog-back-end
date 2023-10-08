package com.test.teamlog.domain.task.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.task.entity.Task;
import com.test.teamlog.domain.task.entity.TaskPerformer;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskUpdateResult {
    private Long id;
    private String taskName;
    private int status;
    private LocalDateTime deadline;
    private LocalDateTime updateTime;
    private String updateTimeStr;
    private List<TaskPerformerResult> performers;

    public static TaskUpdateResult from(Task task) {
        TaskUpdateResult response = new TaskUpdateResult();
        response.setId(task.getId());
        response.setTaskName(task.getTaskName());
        response.setStatus(task.getStatus().getValue());
        response.setDeadline(task.getDeadline());
        response.setUpdateTime(task.getUpdateTime());
        response.setUpdateTimeStr(task.getUpdateTime().toString());
        response.setPerformers(
                !CollectionUtils.isEmpty(task.getTaskPerformers()) ?
                        task.getTaskPerformers().stream().map(TaskPerformerResult::from).toList() :
                        new ArrayList<>()
        );

        return response;
    }

    @Data
    static class TaskPerformerResult {
        private String id;
        private String name;
        private String profileImgPath;

        public static TaskPerformerResult from(TaskPerformer performer) {
            final User user = performer.getUser();

            final TaskPerformerResult result = new TaskPerformerResult();
            result.setId(user.getIdentification());
            result.setName(user.getName());

            final FileInfo profileImage = user.getProfileImage();
            if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

            return result;
        }
    }
}
