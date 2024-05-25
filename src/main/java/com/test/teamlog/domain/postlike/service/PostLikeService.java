package com.test.teamlog.domain.postlike.service;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.post.service.query.PostQueryService;
import com.test.teamlog.domain.postlike.dto.PostLikerResult;
import com.test.teamlog.domain.postlike.entity.PostLike;
import com.test.teamlog.domain.postlike.repository.PostLikeRepository;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Account와 Post 간의 중간 테이블에 대한 Service
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
     * @param currentAccount
     * @return
     */
    @Transactional
    public ApiResponse create(Long postId, Account currentAccount) {
        Post post = preparePost(postId);

        if (postLikeRepository.findByPostAndAccount(post, currentAccount).isPresent()) {
            throw new BadRequestException("이미 좋아요를 누른 게시물입니다.");
        }

        final PostLike postLike = PostLike.builder()
                .post(post)
                .account(currentAccount)
                .build();

        postLikeRepository.save(postLike);
        return new ApiResponse(Boolean.TRUE, "포스트 좋아요 성공");
    }

    /**
     * 좋아요 취소
     *
     * @param postId
     * @param currentAccount
     * @return
     */
    @Transactional
    public ApiResponse delete(Long postId, Account currentAccount) {
        final Post post = preparePost(postId);

        PostLike postLike = postLikeRepository.findByPostAndAccount(post, currentAccount)
                .orElseThrow(() -> new BadRequestException("좋아요를 누르지 않은 게시물입니다."));

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
        Post post = preparePost(postId);

        final List<PostLike> postLikeList = postLikeRepository.findAllByPost(post);
        return postLikeList.stream().map(PostLikerResult::from).collect(Collectors.toList());
    }

    private Post preparePost(Long postId) {
        return postQueryService.findById(postId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 게시물입니다. postId: " + postId));
    }
}
