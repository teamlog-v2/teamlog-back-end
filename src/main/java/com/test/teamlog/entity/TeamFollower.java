package com.test.teamlog.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team_follower")
public class TeamFollower {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id",nullable = false)
    private Team team;

    public void setTeam(Team team) {
        if(this.team != null) {
            this.team.getTeamFollowers().remove(this);
        }
        this.team = team;
        team.getTeamFollowers().add(this);
    }
}
