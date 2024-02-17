package com.test.teamlog.domain.task.entity;

import com.test.teamlog.domain.account.model.Account;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Setter @Getter
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

    public void setAccount(Account account) {
        if(this.account != null) {
            this.account.getTaskPerformers().remove(this);
        }
        this.account = account;
        account.getTaskPerformers().add(this);
    }

}
