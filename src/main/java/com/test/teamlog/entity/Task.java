package com.test.teamlog.entity;

import com.test.teamlog.domain.account.model.User;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseTimeEntity{
    @Id
    @GeneratedValue
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

    public void addTaskPerformers(List<TaskPerformer> performers)
    {
        this.taskPerformers.addAll(performers);
    }

    public void removeTaskPerformers(List<TaskPerformer> performers)
    {
        this.taskPerformers.removeAll(performers);
    }

}
