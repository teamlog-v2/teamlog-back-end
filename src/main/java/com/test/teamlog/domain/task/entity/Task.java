package com.test.teamlog.domain.task.entity;

import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(nullable = false)
    private int priority;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "smallint")
    private TaskStatus status;

    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

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

    // TODO: 구현 방향 검토
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
