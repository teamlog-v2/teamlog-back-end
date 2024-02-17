package com.test.teamlog.domain.accountfollow.entity;

import com.test.teamlog.domain.account.model.Account;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "account_follow",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"from_account_id","to_account_id"}
                )
        }
)
public class AccountFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id", nullable = false)
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id", nullable = false)
    private Account toAccount;

    public static AccountFollow create(Account fromAccount, Account toAccount) {
        AccountFollow accountFollow = new AccountFollow();
        accountFollow.setFromAccount(fromAccount);
        accountFollow.setToAccount(toAccount);

        toAccount.getFollowers().add(accountFollow);
        fromAccount.getFollowings().add(accountFollow);

        return accountFollow;
    }
}
