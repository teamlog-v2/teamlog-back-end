package com.test.teamlog.domain.post.service;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.post.dto.*;
import com.test.teamlog.domain.post.repository.PostRepository;
import com.test.teamlog.domain.postmedia.dto.PostMediaResult;
import com.test.teamlog.domain.postupdatehistory.service.PostUpdateHistoryService;
import com.test.teamlog.entity.*;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.*;
import com.test.teamlog.repository.*;
import com.test.teamlog.service.FileStorageService;
import com.test.teamlog.service.ProjectService;
import com.test.teamlog.service.UserFollowService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostUpdateHistoryService postUpdateHistoryService;
    private final UserFollowService userFollowService;
    private final FileStorageService fileStorageService;
    private final ProjectService projectService;
    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final PostMediaRepository postMediaRepository;
    private final PostLikerRepository postLikerRepository;
    private final PostUpdateHistoryRepository postUpdateHistoryRepository;

    public List<PostResult> getPostsByUser(User currentUser) {
        List<PostResult> resultList = new ArrayList<>();
        List<Post> posts = postRepository.findAllByWriter(currentUser);

        for (Post post : posts) {
            resultList.add(convertToPostResult(post, currentUser));
        }
        return resultList;
    }

    public List<PostResult> readAllByFollowingUser(User currentUser) {
        List<UserFollow> userFollowingList = userFollowService.readUserFollowList(currentUser);
        if (CollectionUtils.isEmpty(userFollowingList)) return Collections.emptyList();

        List<User> userFollowings = userFollowingList.stream().map(UserFollow::getToUser).collect(Collectors.toList());
        List<Post> posts = postRepository.findAllByWriters(userFollowings);

        return posts.stream().map(post -> convertToPostResult(post, currentUser)).collect(Collectors.toList());
    }

    // 단일 포스트 조회
    public PostResult readOne(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        // 비공개일 경우 프로젝트 멤버 권한 체크
        if (post.getAccessModifier() == AccessModifier.PRIVATE) {
            projectService.validateUserIsMemberOfProject(post.getProject(), currentUser);
        }

        return convertToPostResult(post, currentUser);
    }

    // 모든 포스트 조회
    public PagedResponse<PostResult> readAll(int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<Post> posts = postRepository.findAll(pageable);

        List<PostResult> resultList
                = posts.stream().map(post -> convertToPostResult(post, currentUser)).collect(Collectors.toList());
        return new PagedResponse<>(resultList, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    // 프로젝트 내 포스트 조회
    private PagedResponse<PostResult> searchPostsByProject(Long projectId, Sort.Direction sort, String cop,
                                                                     Long cursor, int size, User currentUser) {
        Project project = projectService.findOne(projectId);
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

        List<PostResult> resultList = new ArrayList<>();
        for (Post post : posts) {
            resultList.add(convertToPostResult(post, currentUser));
        }

        long totalElements = 0;

        if (isUserMemberOfProject)
            totalElements = postRepository.getPostsCount(project);
        else
            totalElements = postRepository.getPostsCount(project, AccessModifier.PUBLIC);

        return new PagedResponse<>(resultList, 0, posts.getSize(), totalElements,
                0, posts.isLast());
    }

    // 키워드로 게시물 조회
    private PagedResponse<PostResult> searchPostsInProjectByKeyword(Long projectId,
                                                                              String keyword,
                                                                              Sort.Direction sort,
                                                                              String cop,
                                                                              Long cursor,
                                                                              int size,
                                                                              User currentUser) {
        Project project = projectService.findOne(projectId);

        Boolean isUserMemberOfProject = projectService.isUserMemberOfProject(project, currentUser);
        Pageable pageable = PageRequest.of(0, size, sort, "id");

        Slice<Post> posts;
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

        List<PostResult> resultList = new ArrayList<>();
        for (Post post : posts) {
            resultList.add(convertToPostResult(post, currentUser));
        }

        long totalElements;
        if (isUserMemberOfProject)
            totalElements = postRepository.getPostsCountByKeyword(project, keyword);
        else
            totalElements = postRepository.getPostsCountByKeyword(project, keyword, AccessModifier.PUBLIC);

        return new PagedResponse<>(resultList, 0, posts.getSize(), totalElements,
                0, posts.isLast());
    }

    // FIXME: QueryDsl 도입 후 중복 로직을 제거해보자
    public PagedResponse<PostResult> search(Long projectId, PostReadByProjectInput input, User currentUser) {
        input.convertPagingInfo();

        final String keyword = input.getKeyword();
        final List<String> hashtagList = input.getHashtagList();
        final Sort.Direction sort = input.getSort();
        final String comparisonOperator = input.getComparisonOperator();
        final Long cursor = input.getCursor();
        final int size = input.getSize();

        if (keyword != null && hashtagList != null) {
            return searchPostsInProjectByHashtagAndKeyword(projectId, keyword, hashtagList, sort, comparisonOperator, cursor, size, currentUser);
        } else if (keyword != null) {
            return searchPostsInProjectByKeyword(projectId, keyword, sort, comparisonOperator, cursor, size, currentUser);
        } else if (hashtagList != null) {
            return searchPostsInProjectByHashtag(projectId, hashtagList, sort, comparisonOperator, cursor, size, currentUser);
        } else {
            return searchPostsByProject(projectId, sort, comparisonOperator, cursor, size, currentUser);
        }
    }

    // 해시태그 선별 조회 + 키워드 검색
    private PagedResponse<PostResult> searchPostsInProjectByHashtagAndKeyword(Long projectId,
                                                                                        String keyword,
                                                                                        List<String> names,
                                                                                        Sort.Direction sort,
                                                                                        String cop,
                                                                                        Long cursor,
                                                                                        int size,
                                                                                        User currentUser) {
        Project project = projectService.findOne(projectId);
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
        List<PostResult> resultList = new ArrayList<>();
        for (Post post : posts) {
            resultList.add(convertToPostResult(post, currentUser));
        }
        long totalElements = 0;
        if (isUserMemberOfProject) {
            postRepository.getPostsCountByHashtagAndKeyword(project, names, keyword, pageable).getTotalElements();
        } else {
            postRepository.getPostsCountByHashtagAndKeyword(project, names, keyword, AccessModifier.PUBLIC, pageable).getTotalElements();
        }

        return new PagedResponse<>(resultList, 0, posts.getSize(), totalElements,
                0, posts.isLast());
    }

    // 해시태그 선별 조회
    private PagedResponse<PostResult> searchPostsInProjectByHashtag(Long projectId, List<String> names, Sort.Direction sort,
                                                                              String cop, Long cursor, int size, User currentUser) {
        Project project = projectService.findOne(projectId);
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
        List<PostResult> resultList = new ArrayList<>();
        for (Post post : posts) {
            resultList.add(convertToPostResult(post, currentUser));
        }
        long totalElements = 0;
        if (isUserMemberOfProject)
            totalElements = postRepository.getPostsCountByHashTag(project, names, pageable).getTotalElements();
        else
            totalElements = postRepository.getPostsCountByHashTag(project, names, AccessModifier.PUBLIC, pageable).getTotalElements();


        return new PagedResponse<>(resultList, 0, posts.getSize(), totalElements,
                0, posts.isLast());
    }

    // 프로젝트의 해시태그들 조회
    public List<String> getHashTagsInProjectPosts(Long id) {
        Project project = projectService.findOne(id);

        List<String> hashtags = postTagRepository.getHashTagsInProjectPosts(project);

        return hashtags;
    }

    // 해시태그 추천
    public List<String> getRecommendedHashTags(Long id) {
        Project project = projectService.findOne(id);

        List<PostTagInfo> hashtags = postTagRepository.getRecommendedHashTags(id);
        List<String> response = new ArrayList<>();
        for (PostTagInfo tag : hashtags) {
            response.add(tag.getName());
        }
        return response;
    }

    // 위치정보가 있는 Public 포스트들 조회
    public List<PostResult> readAllWithLocation(User currentUser) {
        List<Post> posts = postRepository.findAllByLocationIsNotNullAndAccessModifier(AccessModifier.PUBLIC);

        return posts.stream().map(post -> convertToPostResult(post, currentUser)).collect(Collectors.toList());
    }

    // 위치정보가 있는 프로젝트의 포스트들 조회
    public List<PostResult> readAllWithLocation(Long projectId, User currentUser) {
        Project project = projectService.findOne(projectId);
        Boolean isUserMemberOfProject = projectService.isUserMemberOfProject(project, currentUser);

        List<Post> posts = null;
        if (isUserMemberOfProject)
            posts = postRepository.findAllPostsWithLocationByProject(project);
        else
            posts = postRepository.findAllPostsWithLocationByProject(project, AccessModifier.PUBLIC);

        List<PostResult> resultList = new ArrayList<>();
        for (Post post : posts) {
            resultList.add(convertToPostResult(post, currentUser));
        }

        return resultList;
    }


    // 포스트 생성
    @Transactional
    public Long create(PostCreateInput input,
                       MultipartFile[] media,
                       MultipartFile[] files,
                       User currentUser) {
        Project project = projectService.findOne(input.getProjectId());
        projectService.validateUserIsMemberOfProject(project, currentUser);

        input.setLocation(makeLocation(input.getLatitude(), input.getLongitude()));
        Post post = input.toPost(project, currentUser);

        if (!CollectionUtils.isEmpty(input.getHashtags())) {
            final List<PostTag> postTagList
                    = input.getHashtags()
                    .stream().map(hashTag -> PostTag.builder().name(hashTag).build())
                    .collect(Collectors.toList());
            postTagList.forEach(tag -> tag.addPost(post));
        }

        Post newPost = postRepository.save(post);

        storeMediaFiles(media, post);
        storeFiles(files, post);

        postUpdateHistoryService.createPostUpdateHistory(currentUser, post);

        project.setUpdateTime(LocalDateTime.now());
        return newPost.getId();
    }

    private void storeFiles(MultipartFile[] files, Post post) {
        if (files == null) return;

        for (MultipartFile file : files) {
            fileStorageService.storeFile(file, post, Boolean.FALSE);
        }
    }

    private void storeMediaFiles(MultipartFile[] media, Post post) {
        if (media == null) return;

        for (MultipartFile file : media) {
            fileStorageService.storeFile(file, post, Boolean.TRUE);
        }
    }

    // 포스트 수정
    @Transactional
    public Long update(Long id,
                       PostUpdateInput input,
                       MultipartFile[] media,
                       MultipartFile[] files, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        projectService.validateUserIsMemberOfProject(post.getProject(), currentUser);

        post.update(input.getContents(), input.getAccessModifier(), input.getCommentModifier(), makeLocation(input.getLatitude(), input.getLongitude()), input.getAddress());

        // 취소한 파일 삭제 후 새로운 파일 저장
        fileStorageService.deleteFileById(input.getDeletedFileIdList());
        storeMediaFiles(media, post);
        storeFiles(files, post);

        updatePostTagList(input.getHashtags(), post);

        postUpdateHistoryService.createPostUpdateHistory(currentUser, post);

        return post.getId();
    }

    private static Point makeLocation(Double latitude, Double longitude) {
        Point location = null;
        if (latitude != null && longitude != null) {
            location = new GeometryFactory().createPoint(new Coordinate(latitude, longitude));
        }

        return location;
    }

    private void updatePostTagList(List<String> inputHashTagNameList, Post post) {
        List<PostTag> originalHashTags = post.getHashtags();

        if (CollectionUtils.isEmpty(inputHashTagNameList)) {
            post.removeHashTags(originalHashTags);
        } else {
            List<String> maintainedHashTagNameList = new ArrayList<>(); // 기존 해시태그
            List<PostTag> deletedHashTags = new ArrayList<>(); // 삭제된 해시태그

            for (PostTag tag : originalHashTags) {
                final String tagName = tag.getName();

                if (inputHashTagNameList.contains(tagName)) {
                    maintainedHashTagNameList.add(tagName);
                } else {
                    deletedHashTags.add(tag);
                }
            }

            inputHashTagNameList.removeAll(maintainedHashTagNameList);
            List<PostTag> hashtags = inputHashTagNameList.stream()
                    .map(tagName -> PostTag.builder()
                            .name(tagName)
                            .post(post)
                            .build()).collect(Collectors.toList());

            // FIXME: N번 쿼리 날리는 현상. 추후 수정 필요
            post.removeHashTags(deletedHashTags);
            post.addHashTags(hashtags);
        }
    }

    // 포스트 삭제
    @Transactional
    public ApiResponse delete(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        projectService.validateUserIsMemberOfProject(post.getProject(), currentUser);

        fileStorageService.deleteFilesByPost(post);
        postRepository.delete(post);
        return new ApiResponse(Boolean.TRUE, "포스트 삭제 성공");
    }

    // Post to PostResult
    private PostResult convertToPostResult(Post post, User currentUser) {
        final PostResult result = PostResult.of(post);

        // 해시태그 설정
        List<PostTag> hashtagList = postTagRepository.findAllByPost(post);
        List<String> hashtagNameList
                = !CollectionUtils.isEmpty(hashtagList) ?
                hashtagList.stream().map(PostTag::getName).collect(Collectors.toList()) :
                Collections.emptyList();
        result.setHashtags(hashtagNameList);
        
        // 좋아요 여부 설정
        Boolean isILikeIt = currentUser != null ? isILikeIt(post, currentUser) : Boolean.FALSE;
        result.setIsILikeIt(isILikeIt);
        
        // 미디어 정보 설정
        List<PostMediaResult> mediaList = new ArrayList<>();
        List<PostMediaResult> fileList = new ArrayList<>();

        List<PostMedia> postMediaList = postMediaRepository.findAllByPost(post);
        for (PostMedia postMedia : postMediaList) {
            final PostMediaResult postMediaResult = PostMediaResult.from(postMedia);

            if (postMedia.getIsMedia()) {
                postMediaResult.setFileDownloadUri(makeFileDownloadUri(postMedia, "/resources/"));
                mediaList.add(postMediaResult);
            } else {
                postMediaResult.setFileDownloadUri(makeFileDownloadUri(postMedia, "/api/downloadFile/"));
                fileList.add(postMediaResult);
            }
        }
        
        result.setMedia(mediaList);
        result.setFiles(fileList);
        
        return result;
    }

    @Transactional
    public List<PostDTO.PostHistoryInfo> readPostUpdateHistory(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        projectService.validateUserIsMemberOfProject(post.getProject(), currentUser);

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<PostUpdateHistory> historyList = postUpdateHistoryRepository.findAllByPost(post, sort);

        return historyList.stream().map(PostDTO.PostHistoryInfo::new).collect(Collectors.toList());
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
                .orElseThrow(() -> new ResourceNotFoundException("PostLiker", "UserId", currentUser.getIdentification()));

        postLikerRepository.delete(postLiker);
        return new ApiResponse(Boolean.TRUE, "포스트 좋아요 취소 성공");
    }

    // 포스트 좋아요 목록 조회
    public List<UserRequest.UserSimpleInfo> getPostLikerList(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        List<PostLiker> postLikers = postLikerRepository.findAllByPost(post);
        List<UserRequest.UserSimpleInfo> response = new ArrayList<>();
        for (PostLiker postLiker : postLikers) {
            UserRequest.UserSimpleInfo temp = new UserRequest.UserSimpleInfo(postLiker.getUser());
            response.add(temp);
        }
        return response;
    }

    private String makeFileDownloadUri(PostMedia postMedia, String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path)
                .path(postMedia.getStoredFileName())
                .toUriString();
    }

    private Boolean isILikeIt(Post post, User currentUser) {
        return postLikerRepository.findByPostAndUser(post, currentUser).isPresent();
    }
}
