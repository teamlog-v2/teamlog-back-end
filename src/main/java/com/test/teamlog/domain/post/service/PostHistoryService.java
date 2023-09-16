package com.test.teamlog.domain.post.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.post.service.query.PostQueryService;
import com.test.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;
import com.test.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import com.test.teamlog.payload.PostDTO;
import com.test.teamlog.domain.postupdatehistory.repository.PostUpdateHistoryRepository;
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
    public List<PostDTO.PostHistoryInfo> readPostUpdateHistory(Long postId, User currentUser) {
        Post post = postQueryService.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        projectMemberQueryService.validateProjectMember(post.getProject(), currentUser);

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<PostUpdateHistory> historyList = postUpdateHistoryRepository.findAllByPost(post, sort);

        return historyList.stream().map(PostDTO.PostHistoryInfo::new).collect(Collectors.toList());
    }
}
