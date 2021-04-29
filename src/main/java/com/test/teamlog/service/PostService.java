package com.test.teamlog.service;

import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.PostTag;
import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.User;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.PostDTO;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

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
                .contents(post.getContents())
                .hashtags(hashtags)
                .likeCount(post.getPostLikers().size())
                .commentCount(post.getComments().size())
                .writeTime(post.getCreateTime())
                .build();

        return postResponse;
    }

    // 포스트 생성
    @Transactional
    public ApiResponse createPost(PostDTO.PostRequest request, MultipartFile[] files){
        User writer = userRepository.findById(request.getWriterId())
                .orElseThrow(()-> new ResourceNotFoundException("USER","id",request.getWriterId()));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", request.getProjectId()));
        System.out.println(files[0].getOriginalFilename());
        Post post = Post.builder()
                .contents(request.getContents())
                .accessModifier(request.getAccessModifier())
                .commentModifier(request.getCommentModifier())
                .location(request.getLocation())
                .writer(writer)
                .project(project)
                .build();

        postRepository.save(post);

        if (request.getHashtags() != null) {
            List<PostTag> hashtags = new ArrayList<>();
            for(String tagName : request.getHashtags()) {
                PostTag newTag = PostTag.builder()
                        .name(tagName)
                        .post(post)
                        .build();
                hashtags.add(newTag);
            }
            postTagRepository.saveAll(hashtags);
        }

        if (files != null) {
            Arrays.asList(files)
                    .stream()
                    .map(file -> fileStorageService.storeFile(file,post))
                    .collect(Collectors.toList());
        }

        return new ApiResponse(Boolean.TRUE, "포스트 생성 성공");
    }

    // 포스트 수정
    @Transactional
    public ApiResponse updatePost(Long id, PostDTO.PostRequest request) {
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
        if (request.getHashtags().size() > 0) {
            List<PostTag> hashtags = new ArrayList<>();
            for(String tagName : request.getHashtags()) {
                PostTag newTag = PostTag.builder()
                        .name(tagName)
                        .post(post)
                        .build();
                hashtags.add(newTag);
            }
            postTagRepository.saveAll(hashtags);
        }

        return new ApiResponse(Boolean.TRUE, "포스트 수정 성공");
    }

    // 포스트 삭제
    @Transactional
    public ApiResponse deletePost(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post","id",id));
        fileStorageService.deleteFilesByPost(post);
        postRepository.delete(post);
        return new ApiResponse(Boolean.TRUE,"포스트 삭제 성공");
    }
}
