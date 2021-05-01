package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.*;
import com.test.teamlog.repository.*;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public PostDTO.PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        List<String> hashtags = new ArrayList<>();
        if (post.getHashtags() != null) {
            for (PostTag tag : post.getHashtags())
                hashtags.add(tag.getName());
        }

        List<FileDTO.FileInfo> media = new ArrayList<>();
        List<FileDTO.FileInfo> files = new ArrayList<>();
        if (post.getMedia() != null) {
            for (PostMedia temp : post.getMedia()) {
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/downloadFile/")
                        .path(temp.getStoredFileName())
                        .toUriString();
                FileDTO.FileInfo fileInfo = FileDTO.FileInfo.builder()
                        .contentType(temp.getContentType())
                        .fileDownloadUri(fileDownloadUri)
                        .fileName(temp.getFileName())
                        .build();
                if (temp.getIsMedia())
                    media.add(fileInfo);
                else
                    files.add(fileInfo);
            }
        }

        PostDTO.PostResponse postResponse = PostDTO.PostResponse.builder()
                .id(post.getId())
                .contents(post.getContents())
                .hashtags(hashtags)
                .media(media)
                .files(files)
                .likeCount(post.getPostLikers().size())
                .commentCount(post.getComments().size())
                .writeTime(post.getCreateTime())
                .location(post.getLocation())
                .build();

        return postResponse;
    }

    // 포스트 생성
    @Transactional
    public ApiResponse createPost(PostDTO.PostRequest request, MultipartFile[] media, MultipartFile[] files) {
        User writer = userRepository.findById(request.getWriterId())
                .orElseThrow(() -> new ResourceNotFoundException("USER", "id", request.getWriterId()));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", request.getProjectId()));

        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(request.getLatitude(),request.getLongitude()));

        Post post = Post.builder()
                .contents(request.getContents())
                .accessModifier(request.getAccessModifier())
                .commentModifier(request.getCommentModifier())
                .location(point)
                .writer(writer)
                .project(project)
                .build();

        postRepository.save(post);

        if (request.getHashtags() != null) {
            List<PostTag> hashtags = new ArrayList<>();
            for (String tagName : request.getHashtags()) {
                PostTag newTag = PostTag.builder()
                        .name(tagName)
                        .post(post)
                        .build();
                hashtags.add(newTag);
            }
            postTagRepository.saveAll(hashtags);
        }

        if (media != null) {
            Arrays.asList(media)
                    .stream()
                    .map(file -> fileStorageService.storeFile(file, post, Boolean.TRUE))
                    .collect(Collectors.toList());
        }

        if (files != null) {
            Arrays.asList(files)
                    .stream()
                    .map(file -> fileStorageService.storeFile(file, post, Boolean.FALSE))
                    .collect(Collectors.toList());
        }

        return new ApiResponse(Boolean.TRUE, "포스트 생성 성공");
    }

    // 포스트 수정
    @Transactional
    public ApiResponse updatePost(Long id, PostDTO.PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        post.setContents(request.getContents());
        post.setAccessModifier(request.getAccessModifier());
        post.setCommentModifier(request.getCommentModifier());
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(request.getLatitude(),request.getLongitude()));
        post.setLocation(point);

        postRepository.save(post);

//        if (request.getMedia().size() > 0) {
//            postMediaRepository.saveAll(request.getMedia());
//        }
//
        if (request.getHashtags().size() > 0) {
            List<PostTag> hashtags = new ArrayList<>();
            for (String tagName : request.getHashtags()) {
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
    public ApiResponse deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        fileStorageService.deleteFilesByPost(post);
        postRepository.delete(post);
        return new ApiResponse(Boolean.TRUE, "포스트 삭제 성공");
    }
}
