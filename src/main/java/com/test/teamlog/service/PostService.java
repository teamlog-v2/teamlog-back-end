package com.test.teamlog.service;

import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.PostTag;
import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.User;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.PostDTO;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final PostTagRepository postTagRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // 단일 포스트 조회
    public PostDTO.PostResponse getPost(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post","id",id));

        List<String> hashtags = new ArrayList<>();
        if(post.getHashtags() !=null) {
            for(PostTag tag : post.getHashtags())
                hashtags.add(tag.getName());
        }

        PostDTO.PostResponse postResponse = PostDTO.PostResponse.builder()
                .id(post.getId())
                .content(post.getContents())
                .hashtags(hashtags)
                .likeCount(post.getPostLikers().size())
                .commentCount(post.getComments().size())
                .writeTime(post.getCreateTime())
                .build();

        return postResponse;
    }

    // 포스트 생성
    @Transactional
    public ApiResponse createPost(PostDTO.PostRequest request){
        User writer = userRepository.findById(request.getWriterId())
                .orElseThrow(()-> new ResourceNotFoundException("USER","id",request.getWriterId()));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", request.getProjectId()));

        Post post = Post.builder()
                .contents(request.getContents())
                .accessModifier(request.getAccessModifier())
                .commentModifier(request.getCommentModifier())
                .location(request.getLocation())
                .writer(writer)
                .project(project)
                .build();

        postRepository.save(post);

//        if (request.getMedia().size() > 0) {
//            postMediaRepository.saveAll(request.getMedia());
//        }

//        if (request.getHashtags().size() > 0) {
//            postTagRepository.saveAll(request.getHashtags());
//        }

        return new ApiResponse(Boolean.TRUE, "포스트 생성 성공");
    }

    // 포스트 수정
    @Transactional
    public void updatePost(Long id, PostDTO.PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post","id",id));
        post.setContents(request.getContents());
        post.setAccessModifier(request.getAccessModifier());
        post.setCommentModifier(request.getCommentModifier());
        post.setLocation(request.getLocation());

        postRepository.save(post);

//        if (request.getMedia().size() > 0) {
//            postMediaRepository.saveAll(request.getMedia());
//        }
//
//        if (request.getHashtags().size() > 0) {
//            postTagRepository.saveAll(request.getHashtags());
//        }

        postRepository.save(post);
    }

    // 포스트 삭제
    @Transactional
    public ApiResponse deletePost(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post","id",id));

        postRepository.delete(post);
        return new ApiResponse(Boolean.TRUE,"포스트 삭제 성공");
    }
}
