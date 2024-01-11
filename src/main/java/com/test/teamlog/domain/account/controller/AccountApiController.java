package com.test.teamlog.domain.account.controller;

import com.test.teamlog.domain.account.dto.*;
import com.test.teamlog.domain.account.service.AccountService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.security.UserAdapter;
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

import java.io.IOException;
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
    public ResponseEntity<UserValidateResponse> validate(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>(new UserValidateResponse(), HttpStatus.UNAUTHORIZED);
        } else {
            final UserValidateResult result = accountService.validate(currentUser.getUser().getIdx());
            return new ResponseEntity<>(UserValidateResponse.from(result), HttpStatus.OK);
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
    public ResponseEntity<ApiResponse> update(@Valid @RequestPart(value = "key") UserUpdateRequest userRequest,
                                              @RequestPart(value = "profileImg", required = false) MultipartFile image,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        try {
            ApiResponse apiResponse = accountService.updateUser(userRequest, image, currentUser.getUser());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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
        try {
            ApiResponse apiResponse = accountService.updateUserProfileImage(image, currentUser.getUser());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "프로필 이미지 삭제")
    @DeleteMapping("/profile-image")
    public ResponseEntity<ApiResponse> deleteUserProfileImage(@Parameter(hidden = true) @AuthenticationPrincipal UserAdapter currentUser) {
        ApiResponse apiResponse = accountService.deleteUserProfileImage(currentUser.getUser());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
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