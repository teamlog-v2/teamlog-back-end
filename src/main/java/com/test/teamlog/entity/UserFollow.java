package com.test.teamlog.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_follow")
public class UserFollow {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

}
