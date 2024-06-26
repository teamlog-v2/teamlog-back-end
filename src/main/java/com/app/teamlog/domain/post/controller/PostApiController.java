package com.app.teamlog.domain.post.controller;

import com.app.teamlog.domain.post.dto.*;
import com.app.teamlog.domain.post.service.PostService;
import com.app.teamlog.global.dto.CustomPageRequest;
import com.app.teamlog.global.security.AccountAdapter;
import com.app.teamlog.global.dto.ApiResponse;
import com.app.teamlog.global.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<Long> create(@RequestPart(value = "key") PostCreateRequest request,
                                               @RequestPart(value = "media", required = false) MultipartFile[] media,
                                               @RequestPart(value = "files", required = false) MultipartFile[] files,
                                               @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        try {
            Long postId = postService.create(request.toInput(), media, files, currentAccount.getAccount());
            return new ResponseEntity<>(postId, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
                                               @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        try {
            Long postId = postService.update(id, request.toInput(), media, files, currentAccount.getAccount());
            PostResult result = postService.readOne(postId, currentAccount.getAccount());
            return new ResponseEntity<>(PostResponse.from(result), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id,
                                              @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        ApiResponse apiResponse = postService.delete(id, currentAccount.getAccount());

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "게시물 조회")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> readOne(@PathVariable("id") long id,
                                                @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        PostResult result = postService.readOne(id, currentAccount.getAccount());

        return new ResponseEntity<>(PostResponse.from(result), HttpStatus.OK);
    }


    // TODO: API 사용 여부 확인
    @Operation(summary = "모든 게시물 조회")
    @GetMapping
    public ResponseEntity<PagedResponse<PostResponse>> readAll(@ModelAttribute CustomPageRequest pageRequest,
                                                               @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        PagedResponse<PostResult> result = postService.readAll(pageRequest.toPageRequest(), currentAccount.getAccount());
        final List<PostResponse> responseList = result.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        final PagedResponse<PostResponse> response
                = new PagedResponse<>(responseList, result.getPage(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "위치정보가 있는 프로젝트 게시물 조회")
    @GetMapping("/with-location")
    public ResponseEntity<List<PostResponse>> readAllWithLocation(@Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        List<PostResult> result = postService.readAllWithLocation(currentAccount.getAccount());
        List<PostResponse> response = result.stream().map(PostResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "프로젝트의 게시물 조회(검색)")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<PagedResponse<PostResponse>> search(@PathVariable("projectId") long projectId,
                                                              @ModelAttribute PostReadByProjectRequest request,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        final PagedResponse<PostResult> result = postService.search(projectId, request.toInput(), currentAccount.getAccount());

        final List<PostResponse> responseList = result.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        final PagedResponse<PostResponse> response
                = new PagedResponse<>(responseList, result.getPage(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "팔로우 중인 사람들의 게시물 조회")
    @GetMapping("/following-accounts")
    public ResponseEntity<List<PostResponse>> readAllByFollowingAccount(@Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        List<PostResult> result = postService.readAllByFollowingAccount(currentAccount.getAccount());
        List<PostResponse> response = result.stream().map(PostResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "개인 작성 이력 조회 (게시물)")
    @GetMapping("/accounts/posts")
    public ResponseEntity<List<PostResponse>> getPostsByAccount(@Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        List<PostResponse> response = null;
        if (currentAccount == null) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            final List<PostResult> resultList = postService.getPostsByAccount(currentAccount.getAccount());
            response = resultList.stream().map(PostResponse::from).collect(Collectors.toList());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Operation(summary = "위치정보가 있는 프로젝트 게시물 조회")
    @GetMapping("/projects/{projectId}/posts/with-location")
    public ResponseEntity<List<PostResponse>> getLocationPosts(@PathVariable("projectId") long projectId,
                                                               @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        List<PostResult> resultList = postService.readAllWithLocation(projectId, currentAccount.getAccount());
        final List<PostResponse> response = resultList.stream().map(PostResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}