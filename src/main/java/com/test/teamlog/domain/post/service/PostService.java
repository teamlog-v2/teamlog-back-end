package com.test.teamlog.domain.post.service;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.file.management.service.FileManagementService;
import com.test.teamlog.domain.post.dto.PostCreateInput;
import com.test.teamlog.domain.post.dto.PostReadByProjectInput;
import com.test.teamlog.domain.post.dto.PostResult;
import com.test.teamlog.domain.post.dto.PostUpdateInput;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.post.repository.PostRepository;
import com.test.teamlog.domain.postmedia.dto.PostMediaResult;
import com.test.teamlog.domain.postmedia.entity.PostMedia;
import com.test.teamlog.domain.posttag.entity.PostTag;
import com.test.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.project.service.query.ProjectQueryService;
import com.test.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.test.teamlog.domain.accountfollow.entity.AccountFollow;
import com.test.teamlog.domain.accountfollow.service.query.AccountFollowQueryService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.dto.PagedResponse;
import com.test.teamlog.global.entity.AccessModifier;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    private final ProjectQueryService projectQueryService;
    private final FileManagementService fileManagementService;
    private final AccountFollowQueryService accountFollowQueryService;
    private final ProjectMemberQueryService projectMemberQueryService;

    public List<PostResult> getPostsByAccount(Account currentAccount) {
        List<PostResult> resultList = new ArrayList<>();
        List<Post> posts = postRepository.findAllByWriter(currentAccount);

        for (Post post : posts) {
            resultList.add(convertToPostResult(post, currentAccount));
        }
        return resultList;
    }

    public List<PostResult> readAllByFollowingAccount(Account currentAccount) {
        List<AccountFollow> accountFollowingList = accountFollowQueryService.readAllByFromAccount(currentAccount);
        if (CollectionUtils.isEmpty(accountFollowingList)) return Collections.emptyList();

        List<Account> accountFollowings = accountFollowingList.stream().map(AccountFollow::getToAccount).collect(Collectors.toList());
        List<Post> posts = postRepository.findAllByWriterIn(accountFollowings);

        return posts.stream().map(post -> convertToPostResult(post, currentAccount)).collect(Collectors.toList());
    }

    // 단일 포스트 조회
    public PostResult readOne(Long id, Account currentAccount) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post"));

        // 비공개일 경우 프로젝트 멤버 권한 체크
        if (post.getAccessModifier() == AccessModifier.PRIVATE) {
            projectMemberQueryService.isProjectMember(post.getProject(), currentAccount);
        }

        return convertToPostResult(post, currentAccount);
    }

    // 모든 포스트 조회
    public PagedResponse<PostResult> readAll(Pageable pageable, Account currentAccount) {
        Page<Post> posts = postRepository.findAll(pageable);

        List<PostResult> resultList
                = posts.stream().map(post -> convertToPostResult(post, currentAccount)).collect(Collectors.toList());
        return new PagedResponse<>(resultList, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    // 해시태그 선별 조회 + 키워드 검색
    public PagedResponse<PostResult> search(Long projectId,
                                            PostReadByProjectInput input,
                                            Account currentAccount) {
        input.convertPagingInfo();

        Project project = findProjectById(projectId);
        input.setProjectId(projectId);

        boolean isAccountMemberOfProject = projectMemberQueryService.isProjectMember(project, currentAccount);
        Pageable pageable = PageRequest.of(0, input.getSize(), input.getSort(), "id");

        input.setAccessModifier(!isAccountMemberOfProject ? AccessModifier.PUBLIC : null);
        final Page<Post> page = postRepository.search(input, pageable);
        final List<Post> postList = page.getContent();

        List<PostResult> resultList = postList.stream().map(p -> convertToPostResult(p, currentAccount)).collect(Collectors.toList());

        return new PagedResponse<>(resultList, 0, page.getSize(), page.getTotalElements(), 0, page.isLast());
    }

    // 위치정보가 있는 Public 포스트들 조회
    public List<PostResult> readAllWithLocation(Account currentAccount) {
        List<Post> posts = postRepository.findAllByLocationIsNotNullAndAccessModifier(AccessModifier.PUBLIC);

        return posts.stream().map(post -> convertToPostResult(post, currentAccount)).collect(Collectors.toList());
    }

    // 위치정보가 있는 프로젝트의 포스트들 조회
    public List<PostResult> readAllWithLocation(Long projectId, Account currentAccount) {
        Project project = findProjectById(projectId);
        Boolean isAccountMemberOfProject = projectMemberQueryService.isProjectMember(project, currentAccount);

        List<Post> posts = null;
        if (isAccountMemberOfProject)
            posts = postRepository.findAllByProjectAndLocationIsNotNull(project);
        else
            posts = postRepository.findAllByProjectAndAccessModifierAndLocationIsNotNull(project, AccessModifier.PUBLIC);

        List<PostResult> resultList = new ArrayList<>();
        for (Post post : posts) {
            resultList.add(convertToPostResult(post, currentAccount));
        }

        return resultList;
    }

    // 포스트 생성
    @Transactional
    public Long create(PostCreateInput input,
                       MultipartFile[] media,
                       MultipartFile[] files,
                       Account currentAccount) throws IOException {
        Project project = findProjectById(input.getProjectId());
        projectMemberQueryService.isProjectMember(project, currentAccount);

        input.setLocation(makeLocation(input.getLatitude(), input.getLongitude()));
        Post post = input.toPost(project, currentAccount);

        if (!CollectionUtils.isEmpty(input.getHashtags())) {
            final List<PostTag> postTagList
                    = input.getHashtags()
                    .stream().map(hashTag -> PostTag.builder().name(hashTag).build())
                    .toList();
            postTagList.forEach(tag -> tag.setPost(post));
        }

        post.addPostUpdateHistory(new PostUpdateHistory(post, currentAccount));

        storeMediaFiles(media, post);
        storeFiles(files, post);

        Post newPost = postRepository.save(post);
        return newPost.getId();
    }

    private void storeFiles(MultipartFile[] files, Post post) throws IOException {
        if (files == null) return;

        List<PostMedia> postMediaList = new ArrayList<>();
        for (MultipartFile file : files) {
            final FileInfo fileInfo = fileManagementService.uploadFile(file);
            postMediaList.add(PostMedia.create(post, false, fileInfo));
        }

        post.addAllPostMedia(postMediaList);
    }

    private void storeMediaFiles(MultipartFile[] media, Post post) throws IOException {
        if (media == null) return;

        List<PostMedia> postMediaList = new ArrayList<>();
        for (MultipartFile file : media) {
            final FileInfo fileInfo = fileManagementService.uploadFile(file);
            postMediaList.add(PostMedia.create(post, true, fileInfo));
        }

        post.addAllPostMedia(postMediaList);
    }


    // 포스트 수정
    @Transactional
    public Long update(Long id,
                       PostUpdateInput input,
                       MultipartFile[] media,
                       MultipartFile[] files, Account currentAccount) throws IOException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post"));
        projectMemberQueryService.isProjectMember(post.getProject(), currentAccount);

        post.update(input.getContents(), input.getAccessModifier(), input.getCommentModifier(), makeLocation(input.getLatitude(), input.getLongitude()), input.getAddress());

        // 취소한 파일 삭제 후 새로운 파일 저장
        storeMediaFiles(media, post);
        storeFiles(files, post);

        updatePostTagList(input.getHashtags(), post);

        return post.getId();
    }

    private static Point makeLocation(Double latitude, Double longitude) {
        Point location = null;
        if (latitude != null && longitude != null) {
            location = new GeometryFactory().createPoint(new Coordinate(longitude, latitude));
        }

        return location;
    }

    private void updatePostTagList(List<String> inputHashTagNameList, Post post) {
        List<PostTag> originalHashTags = post.getHashtagList();

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
    public ApiResponse delete(Long id, Account currentAccount) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post"));
        projectMemberQueryService.isProjectMember(post.getProject(), currentAccount);

        postRepository.delete(post);
        return new ApiResponse(Boolean.TRUE, "포스트 삭제 성공");
    }

    // Post to PostResult
    private PostResult convertToPostResult(Post post, Account currentAccount) {
        final PostResult result = PostResult.of(post);

        // 해시태그 설정
        List<PostTag> hashtagList = post.getHashtagList();
        List<String> hashtagNameList
                = !CollectionUtils.isEmpty(hashtagList) ?
                hashtagList.stream().map(PostTag::getName).collect(Collectors.toList()) :
                Collections.emptyList();
        result.setHashtags(hashtagNameList);

        // 좋아요 여부 설정
        Boolean isILikeIt = currentAccount != null ?
                postRepository.existsPostLikeByPostAndAccount(post.getId(), currentAccount.getIdx()) :
                Boolean.FALSE;
        result.setIsILikeIt(isILikeIt);

        // 미디어 정보 설정
        List<PostMediaResult> mediaList = new ArrayList<>();
        List<PostMediaResult> fileList = new ArrayList<>();

        List<PostMedia> postMediaList = postRepository.findAllPostMediaByPostId(post.getId());
        for (PostMedia postMedia : postMediaList) {
            final PostMediaResult postMediaResult = PostMediaResult.from(postMedia);

            if (postMedia.getIsMedia()) {
                mediaList.add(postMediaResult);
            } else {
                fileList.add(postMediaResult);
            }
        }

        result.setMedia(mediaList);
        result.setFiles(fileList);

        return result;
    }

    private String makeFileDownloadUri(PostMedia postMedia, String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path)
                .path(postMedia.getFileInfo().getStoredFileName())
                .toUriString();
    }

    private Project findProjectById(Long projectId) {
        return projectQueryService.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project"));
    }
}
