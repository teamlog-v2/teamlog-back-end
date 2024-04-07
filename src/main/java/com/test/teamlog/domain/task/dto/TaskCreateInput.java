package com.test.teamlog.domain.task.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.task.entity.Task;
import com.test.teamlog.domain.task.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskCreateInput {
    @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
    @Size(min=1,max=30, message = "태스크 이름을 1자에서 30자 사이로 입력해주세요.")
    private String taskName;
    private Long projectId;
    private TaskStatus status;
    private LocalDateTime deadline;
    private List<String> performerIdList;

    public Task toTask(Project project, Account account) {
        return Task.builder()
                .taskName(taskName)
                .project(project)
                .status(status)
                .deadline(deadline)
                .creator(account)
                .build();
    }
}
