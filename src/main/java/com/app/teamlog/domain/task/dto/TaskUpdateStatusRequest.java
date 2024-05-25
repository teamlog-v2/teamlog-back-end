package com.app.teamlog.domain.task.dto;

import com.app.teamlog.domain.task.entity.TaskStatus;
import lombok.Data;

@Data
public class TaskUpdateStatusRequest {
    private TaskStatus status;
    private Integer priority;
}
