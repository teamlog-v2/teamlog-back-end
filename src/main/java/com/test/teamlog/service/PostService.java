package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.BadRequestException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.*;
import com.test.teamlog.repository.*;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
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
        return convertToPostResponse(post);
    }

    // 모든 포스트 조회
    public PagedResponse<PostDTO.PostResponse> getAllPosts(int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");

        Page<Post> posts = postRepository.findAll(pageable);
        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post));
        }
        return new PagedResponse<>(responses, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    // 프로젝트 내 포스트 조회
    public PagedResponse<PostDTO.PostResponse> getPostsByProject(Long projectId ,int page, int size) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<Post> posts = postRepository.findAllByProject(project, pageable);

        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post));
        }
        return new PagedResponse<>(responses, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    // 키워드로 게시물 조회
    public PagedResponse<PostDTO.PostResponse> searchPostsInProject(Long projectId, String keyword, int page, int size) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<Post> posts = postRepository.searchPostsInProject(project, keyword, pageable);

        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post));
        }
        return new PagedResponse<>(responses, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    // 프로젝트의 해시태그들 조회
    public List<String> getHashTagsInProjectPosts(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        List<String> hashtags = postTagRepository.getHashTagsInProjectPosts(project);

        return hashtags;
    }

    // 해시태그 선별 조회
    public PagedResponse<PostDTO.PostResponse> getPostsInProjectByHashTag(Long projectId, List<String> names, int page, int size) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<Post> posts = postRepository.getPostsInProjectByHashTag(project, names, pageable);

        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post));
        }
        return new PagedResponse<>(responses, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    // 위치정보가 있는 Public 포스트들 조회
    public List<PostDTO.PostResponse> getLocationPosts() {
        List<Post> posts = postRepository.findAllByLocationIsNotNullAndAccessModifier(AccessModifier.PUBLIC);

        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post));
        }
        return responses;
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("페이지 번호가 0보다 작습니다.");
        }

        if (size < 0) {
            throw new BadRequestException("페이지 크기가 0보다 작습니다.");
        }

        if (size > 10) {
            throw new BadRequestException("페이지 크기는 최대 10 입니다. ");
        }
    }

    // 포스트 생성
    @Transactional
    public ApiResponse createPost(PostDTO.PostRequest request, MultipartFile[] media, MultipartFile[] files) {
        User writer = userRepository.findById(request.getWriterId())
                .orElseThrow(() -> new ResourceNotFoundException("USER", "id", request.getWriterId()));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", request.getProjectId()));

        Point point = null;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            point = geometryFactory.createPoint(new Coordinate(request.getLatitude(), request.getLongitude()));
        }

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

        project.setUpdateTime(LocalDateTime.now());
        projectRepository.save(project);

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
        Point point = geometryFactory.createPoint(new Coordinate(request.getLatitude(), request.getLongitude()));
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

    // Post to PostResponse
    public PostDTO.PostResponse convertToPostResponse(Post post) {
        UserDTO.UserSimpleInfo writer = new UserDTO.UserSimpleInfo(post.getWriter());

        List<String> hashtags = new ArrayList<>();
        if (post.getHashtags() != null) {
            for (PostTag tag : post.getHashtags())
                hashtags.add(tag.getName());
        }

        List<FileDTO.FileInfo> media = new ArrayList<>();
        List<FileDTO.FileInfo> files = new ArrayList<>();
        if (post.getMedia() != null) {
            for (PostMedia temp : post.getMedia()) {
                FileDTO.FileInfo fileInfo = FileDTO.FileInfo.builder()
                        .contentType(temp.getContentType())
                        .fileName(temp.getFileName())
                        .build();
                if (temp.getIsMedia()) {
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/resources/")
                            .path(temp.getStoredFileName())
                            .toUriString();
                    fileInfo.setFileDownloadUri(fileDownloadUri);
                    media.add(fileInfo);
                }
                else {
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/api/downloadFile/")
                            .path(temp.getStoredFileName())
                            .toUriString();
                    fileInfo.setFileDownloadUri(fileDownloadUri);
                    files.add(fileInfo);
                }
            }
        }


        PostDTO.PostResponse postResponse = PostDTO.PostResponse.builder()
                .id(post.getId())
                .project(new ProjectDTO.ProjectSimpleInfo(post.getProject()))
                .writer(writer)
                .contents(post.getContents())
                .hashtags(hashtags)
                .media(media)
                .files(files)
                .likeCount(post.getPostLikers().size())
                .commentCount(post.getComments().size())
                .writeTime(post.getCreateTime())
                .build();
        if (post.getLocation() != null) {
            postResponse.setLatitude(post.getLocation().getX());
            postResponse.setLongitude(post.getLocation().getY());
        }

        return postResponse;
    }

}
