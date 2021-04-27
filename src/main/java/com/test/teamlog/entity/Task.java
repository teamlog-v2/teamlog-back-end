package com.test.teamlog.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends BaseTimeEntity{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private TaskStatus status;

    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskPerformer> taskPerformers = new ArrayList<TaskPerformer>();
}
