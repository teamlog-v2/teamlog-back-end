package com.test.teamlog.domain.task.dto;

import com.test.teamlog.domain.task.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class TaskUpdateRequest {
    @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
    @Size(min=1,max=30, message = "태스크 이름을 1자에서 30자 사이로 입력해주세요.")
    private String taskName;
    private TaskStatus status;
    private ZonedDateTime deadline;
    private List<String> performersId;

    public TaskUpdateInput toInput() {
        TaskUpdateInput input = new TaskUpdateInput();
        input.setTaskName(this.taskName);
        input.setStatus(this.status);
        input.setDeadline(this.deadline);
        input.setPerformerIdList(this.performersId);

        return input;
    }
}
