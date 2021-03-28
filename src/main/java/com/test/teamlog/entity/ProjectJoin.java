package com.test.teamlog.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "project_join",
        uniqueConstraints =@UniqueConstraint(columnNames = {"project_id", "user_id"}))
@EntityListeners(AuditingEntityListener.class)
public class ProjectJoin {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "is_accepted",nullable = false)
    private boolean isAccepted = false;

    @Column(name = "is_invited",nullable = false)
    private boolean isInvited;

    @CreatedDate
    @Column(name = "create_time",nullable = false)
    private LocalDateTime createTime;
}
