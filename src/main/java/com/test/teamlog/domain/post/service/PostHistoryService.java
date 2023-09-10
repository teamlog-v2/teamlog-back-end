package com.test.teamlog.domain.post.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;
import com.test.teamlog.domain.project.service.ProjectService;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.PostDTO;
import com.test.teamlog.repository.PostUpdateHistoryRepository;
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

    private final PostService postService;
    private final ProjectService projectService;

    @Transactional
    public List<PostDTO.PostHistoryInfo> readPostUpdateHistory(Long postId, User currentUser) {
        Post post = postService.readById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        projectService.validateProjectMember(post.getProject(), currentUser);

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<PostUpdateHistory> historyList = postUpdateHistoryRepository.findAllByPost(post, sort);

        return historyList.stream().map(PostDTO.PostHistoryInfo::new).collect(Collectors.toList());
    }
}
