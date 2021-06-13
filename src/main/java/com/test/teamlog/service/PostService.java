package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.*;
import com.test.teamlog.repository.*;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
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
    private final PostMediaRepository postMediaRepository;
    private final PostLikerRepository postLikerRepository;
    private final PostUpdateHistoryRepository postUpdateHistoryRepository;
    private final ProjectRepository projectRepository;
    private final UserFollowRepository userFollowRepository;
    private final FileStorageService fileStorageService;
    private final ProjectService projectService;

    public List<PostDTO.PostResponse> getPostsByUser(User currentUser) {
        List<PostDTO.PostResponse> responses = new ArrayList<>();
        List<Post> posts = postRepository.findAllByWriter(currentUser);

        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }
        return responses;
    }

    public List<PostDTO.PostResponse> getPostsByFollowingUser(User currentUser) {
        List<UserFollow> userFollowingList = userFollowRepository.findByFromUser(currentUser);
        List<PostDTO.PostResponse> responses = new ArrayList<>();
        if (userFollowingList == null) return responses;

        List<User> userFollowings = new ArrayList<>();
        for (UserFollow userFollow : userFollowingList) {
            userFollowings.add(userFollow.getToUser());
        }
        List<Post> posts = postRepository.findAllByWriters(userFollowings);

        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }

        return responses;
    }

    // 단일 포스트 조회
    public PostDTO.PostResponse getPost(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        if (post.getAccessModifier() == AccessModifier.PRIVATE) {
            projectService.validateUserIsMemberOfProject(post.getProject(), currentUser);
        }
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
        Boolean isUserMemberOfProject = projectService.isUserMemberOfProject(project, currentUser);

        Slice<Post> posts = null;
        if (cursor == null) {
            if (isUserMemberOfProject)
                posts = postRepository.findAllByProject(project, pageable);
            else
                posts = postRepository.findAllByProject(project, AccessModifier.PUBLIC, pageable);
        } else {
            if (isUserMemberOfProject)
                posts = postRepository.findAllByProjectAndCursor(project, cursor, cop, pageable);
            else
                posts = postRepository.findAllByProjectAndCursor(project, cursor, cop, AccessModifier.PUBLIC, pageable);
        }

        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }

        long totalElements = 0;

        if (isUserMemberOfProject)
            totalElements = postRepository.getPostsCount(project);
        else
            totalElements = postRepository.getPostsCount(project, AccessModifier.PUBLIC);

        return new PagedResponse<>(responses, 0, posts.getSize(), totalElements,
                0, posts.isLast());
    }

    // 키워드로 게시물 조회
    public PagedResponse<PostDTO.PostResponse> searchPostsInProject(Long projectId, String keyword, Sort.Direction sort,
                                                                    String cop, Long cursor, int size, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Boolean isUserMemberOfProject = projectService.isUserMemberOfProject(project, currentUser);
        Pageable pageable = PageRequest.of(0, size, sort, "id");

        Slice<Post> posts = null;
        if (cursor == null) {
            if (isUserMemberOfProject)
                posts = postRepository.searchPostsInProject(project, keyword, pageable);
            else
                posts = postRepository.searchPostsInProject(project, keyword, AccessModifier.PUBLIC, pageable);
        } else {
            if (isUserMemberOfProject)
                posts = postRepository.searchPostsInProjectByCursor(project, cursor, keyword, cop, pageable);
            else
                posts = postRepository.searchPostsInProjectByCursor(project, cursor, keyword, cop, AccessModifier.PUBLIC, pageable);
        }

        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }
        long totalElements = 0;
        if (isUserMemberOfProject)
            totalElements = postRepository.getPostsCountByKeyword(project, keyword);
        else
            totalElements = postRepository.getPostsCountByKeyword(project, keyword, AccessModifier.PUBLIC);

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
        Boolean isUserMemberOfProject = projectService.isUserMemberOfProject(project, currentUser);
        Pageable pageable = PageRequest.of(0, size, sort, "id");

        Slice<Post> posts = null;
        if (cursor == null) {
            if (isUserMemberOfProject)
                posts = postRepository.searchPostsInProjectByHashtagAndKeyword(project, names, keyword, pageable);
            else
                posts = postRepository.searchPublicPostsInProjectByHashtagAndKeyword(project, names, keyword, AccessModifier.PUBLIC, pageable);
        } else {
            if (isUserMemberOfProject)
                posts = postRepository.searchPostsInProjectByHashtagAndKeywordAndCursor(project, cursor, names,
                        keyword, cop, pageable);
            else
                posts = postRepository.searchPublicPostsInProjectByHashtagAndKeywordAndCursor(project, cursor, names,
                        keyword, cop, AccessModifier.PUBLIC, pageable);
        }
        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }
        long totalElements = 0;
        if (isUserMemberOfProject) {
            postRepository.getPostsCountByHashtagAndKeyword(project, names, keyword, pageable).getTotalElements();
        } else {
            postRepository.getPostsCountByHashtagAndKeyword(project, names, keyword, AccessModifier.PUBLIC, pageable).getTotalElements();
        }

        return new PagedResponse<>(responses, 0, posts.getSize(), totalElements,
                0, posts.isLast());
    }

    // 해시태그 선별 조회
    public PagedResponse<PostDTO.PostResponse> getPostsInProjectByHashtag(Long projectId, List<String> names, Sort.Direction sort,
                                                                          String cop, Long cursor, int size, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Pageable pageable = PageRequest.of(0, size, sort, "id");
        Boolean isUserMemberOfProject = projectService.isUserMemberOfProject(project, currentUser);

        Slice<Post> posts = null;
        if (cursor == null) {
            if (isUserMemberOfProject)
                posts = postRepository.getPostsInProjectByHashTag(project, names, pageable);
            else
                posts = postRepository.getPostsInProjectByHashTag(project, names, AccessModifier.PUBLIC, pageable);
        } else {
            if (isUserMemberOfProject)
                posts = postRepository.getPostsInProjectByHashTagAndCursor(project, cursor, names, cop, pageable);
            else
                posts = postRepository.getPostsInProjectByHashTagAndCursor(project, cursor, names, cop, AccessModifier.PUBLIC, pageable);
        }
        List<PostDTO.PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(convertToPostResponse(post, currentUser));
        }
        long totalElements = 0;
        if (isUserMemberOfProject)
            totalElements = postRepository.getPostsCountByHashTag(project, names, pageable).getTotalElements();
        else
            totalElements = postRepository.getPostsCountByHashTag(project, names, AccessModifier.PUBLIC, pageable).getTotalElements();


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

    // 해시태그 추천
    public List<String> getRecommendedHashTags(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        List<PostTagInfo> hashtags = postTagRepository.getRecommendedHashTags(id);
        List<String> response = new ArrayList<>();
        for(PostTagInfo tag : hashtags) {
            response.add(tag.getName());
        }
        return response;
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

    // 위치정보가 있는 프로젝트의 포스트들 조회
    public List<PostDTO.PostResponse> getLocationPosts(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Boolean isUserMemberOfProject = projectService.isUserMemberOfProject(project, currentUser);

        List<Post> posts = null;
        if (isUserMemberOfProject)
            posts = postRepository.findAllPostsWithLocationByProject(project);
        else
            posts = postRepository.findAllPostsWithLocationByProject(project, AccessModifier.PUBLIC);

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
        projectService.validateUserIsMemberOfProject(project, currentUser);

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
                .address(request.getAddress())
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

        // 포스트 수정내역 생성
        PostUpdateHistory history = PostUpdateHistory.builder()
                .post(post)
                .user(currentUser)
                .build();
        postUpdateHistoryRepository.save(history);

        project.setUpdateTime(LocalDateTime.now());
        projectRepository.save(project);

        return newPost.getId();
    }

    // 포스트 수정
    @Transactional
    public ApiResponse updatePost(Long id, PostDTO.PostUpdateRequest request, MultipartFile[] media, MultipartFile[] files, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        projectService.validateUserIsMemberOfProject(post.getProject(), currentUser);

        post.setContents(request.getContents());
        post.setAccessModifier(request.getAccessModifier());
        post.setCommentModifier(request.getCommentModifier());

        Point point = null;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            point = geometryFactory.createPoint(new Coordinate(request.getLatitude(), request.getLongitude()));
        }
        post.setLocation(point);
        post.setAddress(request.getAddress());

        postRepository.save(post);

        // 취소한 파일 삭제
        if (request.getDeletedFileIdList() != null) {
            List<Long> fileIdList = request.getDeletedFileIdList();
            for (Long fileId : fileIdList) {
                fileStorageService.deleteFileById(fileId);
            }
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

        List<PostTag> originalHashTags = null;
        if (post.getHashtags() != null) {
            originalHashTags = post.getHashtags();
        }

        if (request.getHashtags() == null) {
            if (originalHashTags != null) post.removeHashTags(originalHashTags);
        } else {
            List<String> newHashTagNames = request.getHashtags();
            List<String> maintainedHashTagNames = new ArrayList<>();
            if (originalHashTags != null) {
                List<PostTag> deletedHashTags = new ArrayList<>();
                for (PostTag tag : originalHashTags) {
                    if (newHashTagNames.contains(tag.getName())) {
                        maintainedHashTagNames.add(tag.getName());
                    } else {
                        deletedHashTags.add(tag);
                    }
                }
                post.removeHashTags(deletedHashTags);
            }

            newHashTagNames.removeAll(maintainedHashTagNames); // new
            System.out.println(request.getHashtags());
            System.out.println(newHashTagNames);
            if (newHashTagNames.size() > 0) {
                List<PostTag> hashtags = new ArrayList<>();
                for (String tagName : newHashTagNames) {
                    PostTag newTag = PostTag.builder()
                            .name(tagName)
                            .post(post)
                            .build();
                    hashtags.add(newTag);
                }
                post.addHashTags(hashtags);
            }
        }
        // 포스트 수정내역 생성
        PostUpdateHistory history = PostUpdateHistory.builder()
                .post(post)
                .user(currentUser)
                .build();
        postUpdateHistoryRepository.save(history);

        return new ApiResponse(Boolean.TRUE, "포스트 수정 성공");
    }

    // 포스트 삭제
    @Transactional
    public ApiResponse deletePost(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        projectService.validateUserIsMemberOfProject(post.getProject(), currentUser);

        fileStorageService.deleteFilesByPost(post);
        postRepository.delete(post);
        return new ApiResponse(Boolean.TRUE, "포스트 삭제 성공");
    }

    // Post to PostResponse
    public PostDTO.PostResponse convertToPostResponse(Post post, User currentUser) {
        UserDTO.UserSimpleInfo writer = new UserDTO.UserSimpleInfo(post.getWriter());

        List<String> hashtags = new ArrayList<>();
        List<PostTag> hashtagList = postTagRepository.findAllByPost(post);
        if (hashtagList != null) {
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
        if (post.getPostLikers() != null) likeCount = post.getPostLikers().size();
        int commentCount = 0;
        if (post.getComments() != null) commentCount = post.getComments().size();

        Boolean isIlikeIt = Boolean.FALSE;
        if (currentUser != null) isIlikeIt = isILikeIt(post, currentUser);
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
                .writeTimeStr(post.getCreateTime().toString())
                .build();
        if (post.getLocation() != null) {
            postResponse.setLatitude(post.getLocation().getX());
            postResponse.setLongitude(post.getLocation().getY());
            postResponse.setAddress(post.getAddress());
        }

        return postResponse;
    }

    @Transactional
    public List<PostDTO.PostHistoryInfo> getPostUpdateHistory(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        projectService.validateUserIsMemberOfProject(post.getProject(), currentUser);

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<PostUpdateHistory> historyList = postUpdateHistoryRepository.findAllByPost(post, sort);
        List<PostDTO.PostHistoryInfo> historyResponse = new ArrayList<>();
        for (PostUpdateHistory history : historyList) {
            historyResponse.add(new PostDTO.PostHistoryInfo(history));
        }
        return historyResponse;
    }


    // -------------------------------
    // ------- 포스트 좋아요 관리 -------
    // -------------------------------
    // 좋아요
    @Transactional
    public ApiResponse likePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        PostLiker postLiker = PostLiker.builder()
                .post(post)
                .user(currentUser)
                .build();

        try {
            postLikerRepository.saveAndFlush(postLiker);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("좋아요는 한번만 가능합니다.");
        }

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
    public List<UserDTO.UserSimpleInfo> getPostLikerList(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        List<PostLiker> postLikers = postLikerRepository.findAllByPost(post);
        List<UserDTO.UserSimpleInfo> response = new ArrayList<>();
        for (PostLiker postLiker : postLikers) {
            UserDTO.UserSimpleInfo temp = new UserDTO.UserSimpleInfo(postLiker.getUser());
            response.add(temp);
        }
        return response;
    }

    public Boolean isILikeIt(Post post, User currentUser) {
        return postLikerRepository.findByPostAndUser(post, currentUser).isPresent();
    }
}
