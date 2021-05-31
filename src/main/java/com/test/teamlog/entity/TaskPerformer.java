package com.test.teamlog.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_performer")
public class TaskPerformer {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id",nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    public void setTask(Task task) {
        if(this.task != null) {
            this.task.getTaskPerformers().remove(this);
        }
        this.task = task;
        task.getTaskPerformers().add(this);
    }

    public void setUser(User user) {
        if(this.user != null) {
            this.user.getTaskPerformers().remove(this);
        }
        this.user = user;
        user.getTaskPerformers().add(this);
    }

}
