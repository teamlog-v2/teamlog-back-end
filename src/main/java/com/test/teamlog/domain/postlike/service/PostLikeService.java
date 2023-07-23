package com.test.teamlog.domain.postlike.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.postlike.dto.PostLikerResult;
import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.PostLike;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Transactional
    public void create(Post post, User user) {
        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        try {
            postLikeRepository.save(postLike);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("좋아요는 한번만 가능합니다.");
        }
    }

    @Transactional
    public void delete(Post post, User user) {
        PostLike postLike = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new ResourceNotFoundException("PostLiker", "UserId", user.getIdentification()));

        postLikeRepository.delete(postLike);
    }

    @Transactional(readOnly = true)
    public List<PostLikerResult> readAllByPost(Post post) {
        final List<PostLike> postLikeList = postLikeRepository.findAllByPost(post);
        return postLikeList.stream().map(PostLikerResult::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Boolean existsByPostAndUser(Post post, User currentUser) {
        return postLikeRepository.findByPostAndUser(post, currentUser).isPresent();
    }
}
