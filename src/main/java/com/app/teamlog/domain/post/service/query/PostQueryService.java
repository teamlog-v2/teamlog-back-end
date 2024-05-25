package com.app.teamlog.domain.post.service.query;

import com.app.teamlog.domain.post.entity.Post;
import com.app.teamlog.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostQueryService {
    private final PostRepository postRepository;

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }
}
