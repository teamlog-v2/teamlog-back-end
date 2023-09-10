package com.test.teamlog.domain.account.controller;

import com.test.teamlog.domain.account.dto.*;
import com.test.teamlog.domain.account.service.AccountService;
import com.test.teamlog.domain.comment.dto.CommentInfoResponse;
import com.test.teamlog.domain.comment.service.CommentService;
import com.test.teamlog.domain.post.dto.PostResponse;
import com.test.teamlog.domain.post.dto.PostResult;
import com.test.teamlog.domain.post.service.PostService;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@Tag(name = "유저 관리")
public class AccountApiController {

    @Value("${cookie.domain}")
    private String cookieDomain;
    private final AccountService accountService;
    private final PostService postService;
    private final CommentService commentService;

    @Operation(summary = "로그인")
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest request,
                                                 HttpServletResponse httpServletResponse) {
        final SignInResult result = accountService.signIn(request.toInput());

        ResponseCookie cookie = ResponseCookie.from("Refresh-Token", result.getRefreshToken())
                .domain(cookieDomain)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
        httpServletResponse.addHeader(SET_COOKIE, cookie.toString());

        return new ResponseEntity<>(SignInResponse.of(result), HttpStatus.OK);
    }

    @Operation(summary = "로그인 검증")
    @GetMapping("/validate")
    public ResponseEntity<UserValidateResponse> validateUser(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>(new UserValidateResponse(), HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(UserValidateResponse.of(currentUser.getUser()), HttpStatus.OK);
        }
    }

    @Operation(summary = "회원 가입")
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        final SignUpResult result = accountService.signUp(request.toInput());
        return new ResponseEntity<>(SignUpResponse.from(result), HttpStatus.CREATED);
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/{identification}")
    public ResponseEntity<UserReadDetailResponse> readDetail(@PathVariable("identification") String identification,
                                                             @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        UserReadDetailResult result = accountService.readDetail(identification, currentUser.getUser());
        return new ResponseEntity<>(UserReadDetailResponse.from(result), HttpStatus.OK);
    }

    @Operation(summary = "회원 정보 수정")
    @PutMapping
    public ResponseEntity<ApiResponse> update(@Valid @RequestPart(value = "key") UserRequest.UserUpdateRequest userRequest,
                                              @RequestPart(value = "profileImg", required = false) MultipartFile image,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = accountService.updateUser(userRequest, image, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "회원 삭제")
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteUser(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = accountService.deleteUser(currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로필 이미지 변경")
    @PutMapping("/profile-image")
    public ResponseEntity<ApiResponse> updateUserProfileImage(@RequestPart(value = "profileImg", required = true) MultipartFile image,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = accountService.updateUserProfileImage(image, currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "프로필 이미지 삭제")
    @DeleteMapping("/profile-image")
    public ResponseEntity<ApiResponse> deleteUserProfileImage(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = accountService.deleteUserProfileImage(currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "개인 작성 이력 조회 (게시물)")
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getPostsByUser(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<PostResponse> response = null;
        if (currentUser == null) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            final List<PostResult> resultList = postService.getPostsByUser(currentUser.getUser());
            response = resultList.stream().map(PostResponse::from).collect(Collectors.toList());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Operation(summary = "개인 작성 이력 조회 (댓글)")
    @GetMapping("/comments")
    public ResponseEntity<List<CommentInfoResponse>> getCommentsByUser(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        List<CommentInfoResponse> response = null;

        if (currentUser == null) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            response = commentService.getCommentByUser(currentUser.getUser());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Operation(summary = "회원 검색")
    @GetMapping
    public ResponseEntity<List<UserSearchResponse>> search(@RequestParam(value = "id", required = false, defaultValue = "") String identification,
                                                                   @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                                   @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        final List<UserSearchResult> resultList = accountService.search(identification, name);
        return new ResponseEntity<>(resultList.stream().map(UserSearchResponse::from).collect(Collectors.toList()), HttpStatus.OK);
    }
}