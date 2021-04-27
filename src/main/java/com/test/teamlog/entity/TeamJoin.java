package com.test.teamlog.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "team_join",
        uniqueConstraints =@UniqueConstraint(columnNames = {"team_id", "user_id"}))
public class TeamJoin extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id",nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "is_accepted",nullable = false)
    private boolean isAccepted = false;

    @Column(name = "is_invited",nullable = false)
    private boolean isInvited;
}
