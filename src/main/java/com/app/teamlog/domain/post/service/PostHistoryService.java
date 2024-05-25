package com.app.teamlog.domain.post.service;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.post.dto.PostHistoryResponse;
import com.app.teamlog.domain.post.entity.Post;
import com.app.teamlog.domain.post.service.query.PostQueryService;
import com.app.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;
import com.app.teamlog.domain.postupdatehistory.repository.PostUpdateHistoryRepository;
import com.app.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.app.teamlog.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostHistoryService {
    private final PostUpdateHistoryRepository postUpdateHistoryRepository;

    private final PostQueryService postQueryService;
    private final ProjectMemberQueryService projectMemberQueryService;

    @Transactional
    public List<PostHistoryResponse> readPostUpdateHistory(Long postId, Account currentAccount) {
        Post post = preparePost(postId);
        projectMemberQueryService.validateProjectMember(post.getProject(), currentAccount);

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<PostUpdateHistory> historyList = postUpdateHistoryRepository.findAllByPost(post, sort);

        return historyList.stream().map(PostHistoryResponse::from).collect(Collectors.toList());
    }

    private Post preparePost(Long postId) {
        return postQueryService.findById(postId)
                .orElseThrow(() -> new BadRequestException("Post" + postId + "가 존재하지 않습니다."));
    }
}
