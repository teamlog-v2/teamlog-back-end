package com.test.teamlog.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "team_member")
public class TeamMember {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id",nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "join_time",nullable = false)
    private LocalDateTime joinTime;

    public void setTeam(Team team) {
        if(this.team != null) {
            this.team.getTeamMembers().remove(this);
        }
        this.team = team;
        team.getTeamMembers().add(this);
    }

}
