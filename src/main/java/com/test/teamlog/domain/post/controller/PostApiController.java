package com.test.teamlog.domain.post.controller;

import com.test.teamlog.domain.post.dto.*;
import com.test.teamlog.domain.post.service.PostService;
import com.test.teamlog.domain.postlike.dto.PostLikerResponse;
import com.test.teamlog.domain.postlike.dto.PostLikerResult;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.PagedResponse;
import com.test.teamlog.payload.PostDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Tag(name = "게시물 관리")
public class PostApiController {
    private final PostService postService;

    @Operation(summary = "게시물 생성")
    @PostMapping
    public ResponseEntity<PostResponse> create(@RequestPart(value = "key") PostRequest request,
                                               @RequestPart(value = "media", required = false) MultipartFile[] media,
                                               @RequestPart(value = "files", required = false) MultipartFile[] files,
                                               @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        Long newPostId = postService.create(request.toInput(), media, files, currentUser.getUser());
        PostResult result = postService.readOne(newPostId, currentUser.getUser());
        return new ResponseEntity<>(PostResponse.from(result), HttpStatus.CREATED);
    }

    @Operation(summary = "게시물 수정")
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable("id") long id,
                                               @Parameter(name = "생성 리퀘스트 + deletedFileIdList 추가됨.\n" +
                                                       "List<Long> 타입이고 삭제할 파일 id를 모아서 보내주면됨\n" +
                                                       "(포스트 조회시 file, media 안에 id도 같이 보내도록 바꿈. 그걸 보내주면 될듯)"
                                               ) @RequestPart(value = "key") PostUpdateRequest request,
                                               @RequestPart(value = "media", required = false) MultipartFile[] media,
                                               @RequestPart(value = "files", required = false) MultipartFile[] files,
                                               @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final Long postId = postService.update(id, request.toInput(), media, files, currentUser.getUser());
        PostResult result = postService.readOne(postId, currentUser.getUser());

        return new ResponseEntity<>(PostResponse.from(result), HttpStatus.OK);
    }

    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = postService.delete(id, currentUser.getUser());

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "게시물 조회")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> readOne(@PathVariable("id") long id,
                                                @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        PostResult result = postService.readOne(id, currentUser.getUser());

        return new ResponseEntity<>(PostResponse.from(result), HttpStatus.OK);
    }

    // TODO: API 위치 고민해보기
    @Operation(summary = "게시물 수정 내역 조회")
    @GetMapping("/{id}/historys")
    public ResponseEntity<List<PostDTO.PostHistoryInfo>> readPostUpdateHistory(@PathVariable("id") long id,
                                                                               @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<PostDTO.PostHistoryInfo> response = postService.readPostUpdateHistory(id, currentUser.getUser());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO: API 사용 여부 확인
    @Operation(summary = "모든 게시물 조회")
    @GetMapping
    public ResponseEntity<PagedResponse<PostResponse>> readAll(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                               @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                               @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        PagedResponse<PostResult> result = postService.readAll(page, size, currentUser.getUser());
        final List<PostResponse> responseList = result.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        final PagedResponse<PostResponse> response
                = new PagedResponse<>(responseList, result.getPage(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "위치정보가 있는 프로젝트 게시물 조회")
    @GetMapping("/with-location")
    public ResponseEntity<List<PostResponse>> readAllWithLocation(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<PostResult> result = postService.readAllWithLocation(currentUser.getUser());
        List<PostResponse> response = result.stream().map(PostResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트의 게시물 조회(검색)")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<PagedResponse<PostResponse>> search(@PathVariable("projectId") long projectId,
                                                              @ModelAttribute PostReadByProjectRequest request,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final PagedResponse<PostResult> result = postService.search(projectId, request.toInput(), currentUser.getUser());

        final List<PostResponse> responseList = result.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        final PagedResponse<PostResponse> response
                = new PagedResponse<>(responseList, result.getPage(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "팔로우 중인 사람들의 게시물 조회")
    @GetMapping("/following-users")
    public ResponseEntity<List<PostResponse>> readAllByFollowingUser(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<PostResult> result = postService.readAllByFollowingUser(currentUser.getUser());
        List<PostResponse> response = result.stream().map(PostResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "게시물 좋아요")
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse> like(@PathVariable("postId") long postId,
                                            @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = postService.likePost(postId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "게시물 좋아요 취소")
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse> unlike(@PathVariable("postId") long postId,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = postService.unlikePost(postId, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "게시물을 좋아하는 사람 조회")
    @GetMapping("/{postId}/likers")
    public ResponseEntity<List<PostLikerResponse>> readPostLikerList(@PathVariable("postId") long postId) {
        List<PostLikerResult> resultList = postService.readPostLikerList(postId);
        final List<PostLikerResponse> response = resultList.stream().map(PostLikerResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}