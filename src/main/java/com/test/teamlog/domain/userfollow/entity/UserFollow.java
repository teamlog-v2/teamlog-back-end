package com.test.teamlog.domain.userfollow.entity;

import com.test.teamlog.domain.account.model.User;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "user_follow",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"from_user_id","to_user_id"}
                )
        }
)
public class UserFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    public static UserFollow create(User fromUser, User toUser) {
        UserFollow userFollow = new UserFollow();
        userFollow.setFromUser(fromUser);
        userFollow.setToUser(toUser);

        toUser.getFollowers().add(userFollow);
        fromUser.getFollowings().add(userFollow);

        return userFollow;
    }
}
