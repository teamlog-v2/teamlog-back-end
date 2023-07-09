package com.test.teamlog.domain.postupdatehistory.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.PostUpdateHistory;
import com.test.teamlog.repository.PostUpdateHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostUpdateHistoryService {
    private final PostUpdateHistoryRepository postUpdateHistoryRepository;

    // TODO: Entity 직접 전달...? 전달 방법 고민하기
    public Long createPostUpdateHistory(User currentUser, Post post) {
        PostUpdateHistory history = PostUpdateHistory.builder()
                .post(post)
                .user(currentUser)
                .build();
        final PostUpdateHistory postUpdateHistory = postUpdateHistoryRepository.save(history);

        return postUpdateHistory.getId();
    }
}
