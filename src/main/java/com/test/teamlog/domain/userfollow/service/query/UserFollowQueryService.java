package com.test.teamlog.domain.userfollow.service.query;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.userfollow.entity.UserFollow;
import com.test.teamlog.domain.userfollow.repository.UserFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFollowQueryService {
    private final UserFollowRepository userFollowRepository;

    public List<UserFollow> readAllByFromUser(User currentUser) {
        return userFollowRepository.findAllByFromUser(currentUser);
    }
}
