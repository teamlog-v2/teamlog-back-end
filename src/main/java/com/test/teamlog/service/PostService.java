package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.BadRequestException;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.*;
import com.test.teamlog.repository.*;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
    private final PostMediaRepository postMediaRepository;
    private final PostLikerRepository postLikerRepository;
    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;
    private final ProjectService projectService;

    // 단일 포스트 조회
    public PostDTO.PostResponse getPost(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return convertToPostResponse(post, currentUser);
    }

    // 모든 포스트 조회
    public PagedResponse<PostDTO.PostResponse> getAllPosts(int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(0, size, Sort.Direction.DESC, "createTime");

        Page<Post> posts = postRepository.findAll(pageable);
        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }
        return new PagedResponse<>(responses, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    // 프로젝트 내 포스트 조회
    public PagedResponse<PostDTO.PostResponse> getPostsByProject(Long projectId, Sort.Direction sort, String cop,
                                                                 Long cursor, int size, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Pageable pageable = PageRequest.of(0, size, sort, "id");

        Slice<Post> posts = null;
        if (cursor == null) {
            posts = postRepository.findAllByProject(project, pageable);
        } else {
            posts = postRepository.findAllByProjectAndCursor(project, cursor ,cop, pageable);
        }

        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }

        long totalElements = postRepository.getPostsCount(project);

        return new PagedResponse<>(responses, 0, posts.getSize(), totalElements,
                0, posts.isLast());
    }

    // 키워드로 게시물 조회
    public PagedResponse<PostDTO.PostResponse> searchPostsInProject(Long projectId, String keyword, Sort.Direction sort,
                                                                    String cop, Long cursor, int size, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        Pageable pageable = PageRequest.of(0, size, sort, "id");

        Slice<Post> posts = null;
        if (cursor == null) {
            posts = postRepository.searchPostsInProject(project, keyword, pageable);
        } else {
            posts = postRepository.searchPostsInProjectByCursor(project, cursor, keyword, cop, pageable);
        }

        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }
        long totalElements = postRepository.getPostsCountByKeyword(project, keyword);

        return new PagedResponse<>(responses, 0, posts.getSize(), totalElements,
                0, posts.isLast());
    }

    // 해시태그 선별 조회 + 키워드 검색
    public PagedResponse<PostDTO.PostResponse> searchPostsInProjectByHashtagAndKeyword(Long projectId,
                                                                                       String keyword,
                                                                                       List<String> names,
                                                                                       Sort.Direction sort,
                                                                                       String cop,
                                                                                       Long cursor,
                                                                                       int size,
                                                                                       User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Pageable pageable = PageRequest.of(0, size, sort, "id");

        Slice<Post> posts = null;
        if (cursor == null) {
            posts = postRepository.searchPostsInProjectByHashtagAndKeyword(project, names, keyword, pageable);
        } else {
            posts = postRepository.searchPostsInProjectByHashtagAndKeywordAndCursor(project, cursor, names,
                    keyword, cop, pageable);
        }
        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }
        long totalElements = postRepository.getPostsCountByHashtagAndKeyword(project, names, keyword, pageable).getTotalElements();

        return new PagedResponse<>(responses, 0, posts.getSize(), totalElements,
                0, posts.isLast());
    }

    // 해시태그 선별 조회
    public PagedResponse<PostDTO.PostResponse> getPostsInProjectByHashtag(Long projectId, List<String> names, Sort.Direction sort,
                                                                          String cop, Long cursor, int size, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Pageable pageable = PageRequest.of(0, size, sort, "id");

        Slice<Post> posts = null;
        if (cursor == null) {
            posts = postRepository.getPostsInProjectByHashTag(project, names, pageable);
        } else {
            posts = postRepository.getPostsInProjectByHashTagAndCursor(project, cursor, names, cop, pageable);
        }
        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }
        long totalElements = postRepository.getPostsCountByHashTag(project, names, pageable).getTotalElements();

        return new PagedResponse<>(responses, 0, posts.getSize(), totalElements,
                0, posts.isLast());
    }

    // 프로젝트의 해시태그들 조회
    public List<String> getHashTagsInProjectPosts(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        List<String> hashtags = postTagRepository.getHashTagsInProjectPosts(project);

        return hashtags;
    }

    // 위치정보가 있는 Public 포스트들 조회
    public List<PostDTO.PostResponse> getLocationPosts(User currentUser) {
        List<Post> posts = postRepository.findAllByLocationIsNotNullAndAccessModifier(AccessModifier.PUBLIC);

        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }
        return responses;
    }

    // 포스트 생성
    @Transactional
    public Long createPost(PostDTO.PostRequest request, MultipartFile[] media, MultipartFile[] files, User currentUser) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", request.getProjectId()));
//        projectService.validateUserIsMemberOfProject(project,currentUser);

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
                .writer(currentUser)
                .project(project)
                .build();

        Post newPost = postRepository.save(post);

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
            post.setHashtags(hashtags);
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

        return newPost.getId();
    }

    // TODO : 포스트 수정내역 추가
    // 포스트 수정
    @Transactional
    public ApiResponse updatePost(Long id, PostDTO.PostUpdateRequest request, MultipartFile[] media, MultipartFile[] files, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
//        projectService.validateUserIsMemberOfProject(post.getProject(),currentUser);

        post.setContents(request.getContents());
        post.setAccessModifier(request.getAccessModifier());
        post.setCommentModifier(request.getCommentModifier());
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(request.getLatitude(), request.getLongitude()));
        post.setLocation(point);

        postRepository.save(post);

        // 취소한 파일 삭제
        List<Long> fileIdList = request.getDeletedFileIdList();
        for(Long fileId : fileIdList){
            fileStorageService.deleteFileById(id);
        }

        // 새로운 미디어 추가
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

        // 해시태그 수정
        List<PostTag> originalHashTags = post.getHashtags();
        List<String> originalTags = new ArrayList<>();
        for (PostTag tag : originalHashTags) {
            originalTags.add(tag.getName());
        }

        List<String> newTags = request.getHashtags();
        newTags.removeAll(originalTags); // B-A
        if (request.getHashtags().size() > 0) {
            List<PostTag> hashtags = new ArrayList<>();
            for (String tagName : newTags) {
                PostTag newTag = PostTag.builder()
                        .name(tagName)
                        .post(post)
                        .build();
                hashtags.add(newTag);
            }
            postTagRepository.saveAll(hashtags);
        }

        newTags = request.getHashtags();
        originalTags.removeAll(newTags); // A-B

        for (PostTag tag : originalHashTags) {
            for(String tagName : originalTags){
                if(!tag.getName().equals(tagName)) originalHashTags.remove(tag);
            }
        }
        postTagRepository.deleteAll(originalHashTags);

        return new ApiResponse(Boolean.TRUE, "포스트 수정 성공");
    }

    // 포스트 삭제
    @Transactional
    public ApiResponse deletePost(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
//        projectService.validateUserIsMemberOfProject(post.getProject(),currentUser);

        fileStorageService.deleteFilesByPost(post);
        postRepository.delete(post);
        return new ApiResponse(Boolean.TRUE, "포스트 삭제 성공");
    }

    // Post to PostResponse
    public PostDTO.PostResponse convertToPostResponse(Post post, User currentUser) {
        UserDTO.UserSimpleInfo writer = new UserDTO.UserSimpleInfo(post.getWriter());

        List<String> hashtags = new ArrayList<>();
        if (post.getHashtags() != null) {
            for (PostTag tag : post.getHashtags())
                hashtags.add(tag.getName());
        }

        List<FileDTO.FileInfo> media = new ArrayList<>();
        List<FileDTO.FileInfo> files = new ArrayList<>();
        List<PostMedia> mediaList = postMediaRepository.findAllByPost(post);
        if (mediaList != null) {
            for (PostMedia temp : mediaList) {
                FileDTO.FileInfo fileInfo = FileDTO.FileInfo.builder()
                        .id(temp.getId())
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
                } else {
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/api/downloadFile/")
                            .path(temp.getStoredFileName())
                            .toUriString();
                    fileInfo.setFileDownloadUri(fileDownloadUri);
                    files.add(fileInfo);
                }
            }
        }

        int likeCount = 0;
        if(post.getPostLikers() != null) likeCount = post.getPostLikers().size();
        int commentCount = 0;
        if(post.getComments() != null) commentCount = post.getComments().size();

        Boolean isIlikeIt = isILikeIt(post, currentUser);
        PostDTO.PostResponse postResponse = PostDTO.PostResponse.builder()
                .isILikeIt(isIlikeIt)
                .id(post.getId())
                .project(new ProjectDTO.ProjectSimpleInfo(post.getProject()))
                .writer(writer)
                .accessModifier(post.getAccessModifier())
                .commentModifier(post.getCommentModifier())
                .contents(post.getContents())
                .hashtags(hashtags)
                .media(media)
                .files(files)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .writeTime(post.getCreateTime())
                .build();
        if (post.getLocation() != null) {
            postResponse.setLatitude(post.getLocation().getX());
            postResponse.setLongitude(post.getLocation().getY());
        }

        return postResponse;
    }

    // -------------------------------
    // ------- 포스트 좋아요 관리 -------
    // -------------------------------
    // 좋아요
    @Transactional
    public ApiResponse likePost(Long postId, User currentUser){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        // 좋아요 중복 x
        if(postLikerRepository.findByPostAndUser(post,currentUser).isPresent())
            throw new ResourceAlreadyExistsException("PostLiker","UserId",currentUser.getId());

        PostLiker postLiker = PostLiker.builder()
                .post(post)
                .user(currentUser)
                .build();

        postLikerRepository.save(postLiker);
        return new ApiResponse(Boolean.TRUE, "포스트 좋아요 성공");
    }

    // 좋아요 취소
    @Transactional
    public ApiResponse unlikePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        PostLiker postLiker = postLikerRepository.findByPostAndUser(post, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("PostLiker", "UserId", currentUser.getId()));

        postLikerRepository.delete(postLiker);
        return new ApiResponse(Boolean.TRUE, "포스트 좋아요 취소 성공");
    }

    // 포스트 좋아요 목록 조회
    public List<UserDTO.UserSimpleInfo> getPostLikerList(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        List<PostLiker> postLikers = postLikerRepository.findAllByPost(post);
        List<UserDTO.UserSimpleInfo> response = new ArrayList<>();
        for(PostLiker postLiker : postLikers) {
            UserDTO.UserSimpleInfo temp = new UserDTO.UserSimpleInfo(postLiker.getUser());
            response.add(temp);
        }
        return response;
    }

    public Boolean isILikeIt(Post post, User currentUser) {
        return postLikerRepository.findByPostAndUser(post,currentUser).isPresent();
    }
}
