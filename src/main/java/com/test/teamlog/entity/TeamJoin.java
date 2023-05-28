package com.test.teamlog.entity;

import com.test.teamlog.domain.account.model.User;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private Boolean isAccepted;

    @Column(name = "is_invited",nullable = false)
    private Boolean isInvited;

    public void setTeam(Team team) {
        if(this.team != null) {
            this.team.getTeamJoins().remove(this);
        }
        this.team = team;
        team.getTeamJoins().add(this);
    }
}
