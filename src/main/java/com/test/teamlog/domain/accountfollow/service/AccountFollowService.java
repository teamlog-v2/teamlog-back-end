package com.test.teamlog.domain.accountfollow.service;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.accountfollow.dto.AccountFollowerReadResult;
import com.test.teamlog.domain.accountfollow.dto.AccountFollowingReadResult;
import com.test.teamlog.domain.accountfollow.entity.AccountFollow;
import com.test.teamlog.domain.accountfollow.repository.AccountFollowRepository;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountFollowService {
    private final AccountFollowRepository accountFollowRepository;

    private final AccountQueryService accountQueryService;

    // 팔로워 리스트 조회
    public List<AccountFollowerReadResult> readAllFollower(String accountId, Account currentAccount) {
        final Account account = prepareAccount(accountId);

        List<AccountFollow> followingList = accountFollowRepository.findAllByFromAccount(currentAccount); // 유저가 팔로우하는 사람들 (내가 from)
        List<AccountFollow> followerList = accountFollowRepository.findAllByToAccount(account); // 유저를 팔로우하는 사람들 (내가 to)

        List<AccountFollowerReadResult> resultList = new ArrayList<>();

        for (AccountFollow follower : followerList) {
            AccountFollowerReadResult result = AccountFollowerReadResult.from(follower);

            if (currentAccount == null || follower.getFromAccount().getIdentification().equals(currentAccount.getIdentification())) {
                result.setIsFollow(null);
                resultList.add(result);
                continue;
            }

            result.setIsFollow(Boolean.FALSE);
            for (AccountFollow following : followingList) {
                if (following.getToAccount().equals(follower.getFromAccount())) {
                    result.setIsFollow(Boolean.TRUE);
                    break;
                }
            }

            resultList.add(result);
        }

        return resultList;
    }

    // 팔로잉 리스트 조회
    public List<AccountFollowingReadResult> readAllFollowing(String accountId, Account currentAccount) {
        Account account = prepareAccount(accountId);

        List<AccountFollow> currentAccountFollowingList = accountFollowRepository.findAllByFromAccount(currentAccount); // 로그인한 사람의 팔로잉 목록
        List<AccountFollow> followingList = account.getFollowings(); // 특정 유저의 팔로잉 목록

        List<AccountFollowingReadResult> resultList = new ArrayList<>();

        for (AccountFollow following : followingList) {
            final AccountFollowingReadResult result = AccountFollowingReadResult.from(following);

            // 특정 유저가 팔로우한 사람이 본인일 경우 팔로우 표시를 하지 않는다.
            if (currentAccount == null || following.getToAccount().getIdentification().equals(currentAccount.getIdentification())) {
                result.setIsFollow(null);
                resultList.add(result);
                continue;
            }

            // 특정 사람이 팔로잉한 사람 중 나도 팔로잉 중이면 isFollow = true -> 프론트에서 팔로잉이라고 표시한다.
            result.setIsFollow(Boolean.FALSE);
            for (AccountFollow currentAccountFollowing : currentAccountFollowingList) {
                if (currentAccountFollowing.getToAccount().equals(following.getToAccount())) {
                    result.setIsFollow(Boolean.TRUE);
                    break;
                }
            }

            resultList.add(result);
        }

        return resultList;
    }

    // 팔로우
    @Transactional
    public ApiResponse follow(Account currentAccount, String targetAccountId) {
        Account targetAccount = prepareAccount(targetAccountId);

        accountFollowRepository.save(AccountFollow.create(currentAccount, targetAccount));
        return new ApiResponse(Boolean.TRUE, "팔로우 성공");
    }

    // 언팔로우
    @Transactional
    public ApiResponse unfollow(String targetAccountId, Account currentAccount) {
        Account targetAccount = prepareAccount(targetAccountId);
        AccountFollow accountFollow = prepareAccountFollow(currentAccount, targetAccount);

        accountFollowRepository.delete(accountFollow);

        return new ApiResponse(Boolean.TRUE, "언팔로우 성공");
    }

    public Boolean isFollow(Account fromAccount, Account targetAccount) {
        return accountFollowRepository.findByFromAccountAndToAccount(fromAccount, targetAccount).isPresent();
    }

    private AccountFollow prepareAccountFollow(Account fromAccount, Account toAccount) {
        return accountFollowRepository.findByFromAccountAndToAccount(fromAccount, toAccount)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 팔로우입니다. fromAccount: " + fromAccount.getIdentification() + ", toAccount: " + toAccount.getIdentification()));
    }

    private Account prepareAccount(String identification) {
        return accountQueryService.findByIdentification(identification)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 계정입니다. identification: " + identification));
    }
}
