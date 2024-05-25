package com.app.teamlog.domain.task.entity;

import com.app.teamlog.domain.account.model.Account;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_performer")
public class TaskPerformer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id",nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "account_id",nullable = false)
    private Account account;

    public void setTask(Task task) {
        if(this.task != null) {
            this.task.getTaskPerformers().remove(this);
        }
        this.task = task;
        task.getTaskPerformers().add(this);
    }
}
