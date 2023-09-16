package com.test.teamlog.domain.postlike.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.post.service.query.PostQueryService;
import com.test.teamlog.domain.postlike.dto.PostLikerResult;
import com.test.teamlog.domain.postlike.entity.PostLike;
import com.test.teamlog.domain.postlike.repository.PostLikeRepository;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User와 Post 간의 중간 테이블에 대한 Service
 */
@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;

    private final PostQueryService postQueryService;

    /**
     * 좋아요
     *
     * @param postId
     * @param currentUser
     * @return
     */
    @Transactional
    public ApiResponse create(Long postId, User currentUser) {
        Post post = readPostById(postId);

        if (postLikeRepository.findByPostAndUser(post, currentUser).isPresent()) {
            throw new ResourceAlreadyExistsException("이미 좋아요를 누른 게시물입니다.");
        }

        final PostLike postLike = PostLike.builder()
                .post(post)
                .user(currentUser)
                .build();

        postLikeRepository.save(postLike);
        return new ApiResponse(Boolean.TRUE, "포스트 좋아요 성공");
    }

    /**
     * 좋아요 취소
     *
     * @param postId
     * @param currentUser
     * @return
     */
    @Transactional
    public ApiResponse delete(Long postId, User currentUser) {
        final Post post = readPostById(postId);

        PostLike postLike = postLikeRepository.findByPostAndUser(post, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("PostLiker", "UserId", currentUser.getIdentification()));

        postLikeRepository.delete(postLike);
        return new ApiResponse(Boolean.TRUE, "포스트 좋아요 취소 성공");
    }

    /**
     * 게시물 좋아요 목록 조회
     * @param postId
     * @return
     */
    @Transactional(readOnly = true)
    public List<PostLikerResult> readPostLikerList(Long postId) {
        Post post = readPostById(postId);

        final List<PostLike> postLikeList = postLikeRepository.findAllByPost(post);
        return postLikeList.stream().map(PostLikerResult::from).collect(Collectors.toList());
    }

    private Post readPostById(Long postId) {
        return postQueryService.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
    }
}
