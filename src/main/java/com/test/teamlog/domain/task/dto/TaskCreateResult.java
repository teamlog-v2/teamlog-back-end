package com.test.teamlog.domain.task.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.task.entity.Task;
import com.test.teamlog.domain.task.entity.TaskPerformer;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskCreateResult {
    private Long id;
    private String taskName;
    private int status;
    private LocalDateTime deadline;
    private LocalDateTime updateTime;
    private String updateTimeStr;
    private List<TaskPerformerResult> performers;

    public static TaskCreateResult from(Task task) {
        TaskCreateResult result = new TaskCreateResult();
        result.setId(task.getId());
        result.setTaskName(task.getTaskName());
        result.setStatus(task.getStatus().getValue());
        result.setDeadline(task.getDeadline());
        result.setUpdateTime(task.getUpdateTime());
        result.setPerformers(
                !CollectionUtils.isEmpty(task.getTaskPerformers()) ?
                        task.getTaskPerformers().stream().map(TaskPerformerResult::from).toList() :
                        new ArrayList<>()
        );

        return result;
    }

    @Data
    static class TaskPerformerResult {
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
