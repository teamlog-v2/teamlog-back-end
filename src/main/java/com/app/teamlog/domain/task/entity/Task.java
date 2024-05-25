package com.app.teamlog.domain.task.entity;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "smallint")
    private TaskStatus status;

    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Account creator;

    @Builder.Default
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<TaskPerformer> taskPerformers = new ArrayList<>();

    public void setProject(Project project) {
        if(this.project != null) {
            this.project.getTasks().remove(this);
        }
        this.project = project;
        project.getTasks().add(this);
    }

    public void addTaskPerformerList(List<TaskPerformer> performerList)
    {
        this.taskPerformers.addAll(performerList);
    }

    public void removeTaskPerformerList(List<TaskPerformer> performerList) {
        if (CollectionUtils.isEmpty(performerList)) return;

        this.taskPerformers.removeAll(performerList);
    }

    public void update(String taskName, LocalDateTime deadline) {
        this.taskName = taskName;
        this.deadline = deadline;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
