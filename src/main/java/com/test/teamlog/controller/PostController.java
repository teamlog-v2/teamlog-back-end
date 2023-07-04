package com.test.teamlog.controller;

import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.PagedResponse;
import com.test.teamlog.payload.PostDTO;
import com.test.teamlog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "게시물 관리")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시물 생성")
    @PostMapping("/posts")
    public ResponseEntity<PostDTO.PostResponse> createProject(@RequestPart(value = "key", required = true) PostDTO.PostRequest request,
                                                              @RequestPart(value = "media", required = false) MultipartFile[] media,
                                                              @RequestPart(value = "files", required = false) MultipartFile[] files,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        Long newPostId = postService.createPost(request, media, files, currentUser.getUser());
        PostDTO.PostResponse newPost = postService.getPost(newPostId, currentUser.getUser());
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

    @Operation(summary = "게시물 조회")
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO.PostResponse> getPostById(@PathVariable("id") long id,
                                                            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        PostDTO.PostResponse response = postService.getPost(id, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "게시물 수정")
    @PutMapping("/posts/{id}")
    public ResponseEntity<PostDTO.PostResponse> updateProject(@PathVariable("id") long id,
                                                              @Parameter(name = "생성 리퀘스트 + deletedFileIdList 추가됨.\nList<Long> 타입이고 삭제할 파일 id를 모아서 보내주면됨\n(포스트 조회시 file, media 안에 id도 같이 보내도록 바꿈. 그걸 보내주면 될듯)") @RequestPart(value = "key", required = true) PostDTO.PostUpdateRequest request,
                                                              @RequestPart(value = "media", required = false) MultipartFile[] media,
                                                              @RequestPart(value = "files", required = false) MultipartFile[] files,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = postService.updatePost(id, request, media, files, currentUser.getUser());
        PostDTO.PostResponse updatedPost = postService.getPost(id, currentUser.getUser());
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = postService.deletePost(id, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "게시물 수정 내역 조회")
    @GetMapping("/posts/{id}/historys")
    public ResponseEntity<List<PostDTO.PostHistoryInfo>> getPostUpdateHistory(@PathVariable("id") long id,
                                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<PostDTO.PostHistoryInfo> response = postService.getPostUpdateHistory(id, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "해시태그 추천")
    @GetMapping("/projects/{projectId}/recommended-hashtags")
    public ResponseEntity<List<String>> getRecommendedHashTags(@PathVariable("projectId") long projectId) {
        List<String> response = postService.getRecommendedHashTags(projectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트 내 게시물 전체 해시태그 조회")
    @GetMapping("/projects/{projectId}/hashtags")
    public ResponseEntity<List<String>> getHashTagsInProjectPosts(@PathVariable("projectId") long projectId) {
        List<String> response = postService.getHashTagsInProjectPosts(projectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "모든 게시물 조회")
    @GetMapping("/posts")
    public ResponseEntity<PagedResponse<PostDTO.PostResponse>> getAllPosts(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                           @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        PagedResponse<PostDTO.PostResponse> response = postService.getAllPosts(page, size, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "위치정보가 있는 프로젝트 게시물 조회")
    @GetMapping("/posts/with-location")
    public ResponseEntity<List<PostDTO.PostResponse>> getLocationPosts(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<PostDTO.PostResponse> response = postService.getLocationPosts(currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트의 게시물 조회(검색)")
    @GetMapping("/posts/project/{projectId}")
    public ResponseEntity<PagedResponse<PostDTO.PostResponse>> getPostsByProject(@PathVariable("projectId") long projectId,
                                                                                 @RequestParam(value = "hashtag", required = false) String[] hashtag,
                                                                                 @RequestParam(value = "keyword", required = false) String keyword,
                                                                                 @RequestParam(value = "order", required = false, defaultValue = "1") Integer order,
                                                                                 @RequestParam(value = "cursor", required = false) Long cursor,
                                                                                 @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                                                 @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<String> hashtagList = null;
        if (hashtag != null) hashtagList = Arrays.asList(hashtag);

        Sort.Direction sort = Sort.Direction.DESC;
        String comparisonOperator = "<";
        if (order == -1) {
            sort = Sort.Direction.ASC;
            comparisonOperator = ">";
        }
        PagedResponse<PostDTO.PostResponse> response = null;
        if (keyword != null & hashtagList != null) {
            response = postService.searchPostsInProjectByHashtagAndKeyword(projectId, keyword, hashtagList, sort, comparisonOperator, cursor, size, currentUser.getUser());
        } else if (keyword != null) {
            response = postService.searchPostsInProject(projectId, keyword, sort, comparisonOperator, cursor, size, currentUser.getUser());
        } else if (hashtagList != null) {
            response = postService.getPostsInProjectByHashtag(projectId, hashtagList, sort, comparisonOperator, cursor, size, currentUser.getUser());
        } else {
            response = postService.getPostsByProject(projectId, sort, comparisonOperator, cursor, size, currentUser.getUser());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "위치정보가 있는 프로젝트 게시물 조회")
    @GetMapping("/projects/{projectId}/posts/with-location")
    public ResponseEntity<List<PostDTO.PostResponse>> getLocationPosts(@PathVariable("projectId") long projectId,
                                                                       @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<PostDTO.PostResponse> response = postService.getLocationPosts(projectId, currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "팔로우 중인 사람들의 게시물 조회")
    @GetMapping("/following-users/posts")
    public ResponseEntity<List<PostDTO.PostResponse>> getPostsByFollowingUser(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<PostDTO.PostResponse> response = postService.getPostsByFollowingUser(currentUser.getUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}