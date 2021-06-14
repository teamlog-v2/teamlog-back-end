package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.*;
import com.test.teamlog.security.JwtUtil;
import com.test.teamlog.service.CommentService;
import com.test.teamlog.service.PostService;
import com.test.teamlog.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api(tags = "유저 관리")
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final JwtUtil jwtUtil;


    @ApiOperation(value = "로그인")
    @PostMapping("/sign-in")
    public ResponseEntity<Token> signIn(@RequestBody UserDTO.SignInRequest userRequest,
                                        HttpServletRequest req,
                                        HttpServletResponse res) {
        User user = userService.signIn(userRequest);
        if (user != null) {
            String token = jwtUtil.generateToken(user);
            return new ResponseEntity<>(new Token(token), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "로그인 검증")
    @GetMapping("/validate")
    public ResponseEntity<UserDTO.UserSimpleInfo> validateUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>(new UserDTO.UserSimpleInfo(), HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(new UserDTO.UserSimpleInfo(currentUser), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "회원 가입")
    @PostMapping("/users")
    public ResponseEntity<UserDTO.UserSimpleInfo> signUp(@Valid @RequestBody UserDTO.UserRequest userRequest) {
        UserDTO.UserSimpleInfo userResponse = userService.signUp(userRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "회원 정보 조회")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO.UserResponse> getUserById(@PathVariable("id") String id,
                                                            @ApiIgnore @AuthenticationPrincipal User currentUser) {
        UserDTO.UserResponse userResponse = userService.getUser(id, currentUser);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 정보 수정")
    @PutMapping("/users")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestPart(value = "key", required = true) UserDTO.UserUpdateRequest userRequest,
                                                  @RequestPart(value = "profileImg", required = false) MultipartFile image,
                                                  @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = userService.updateUser(userRequest, image, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 삭제")
    @DeleteMapping("/users")
    public ResponseEntity<ApiResponse> deleteUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = userService.deleteUser(currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "프로필 이미지 변경")
    @PutMapping("/users/profile-image")
    public ResponseEntity<ApiResponse> updateUserProfileImage(@RequestPart(value = "profileImg", required = true) MultipartFile image,
                                                              @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = userService.updateUserProfileImage(image, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "프로필 이미지 삭제")
    @DeleteMapping("/users/profile-image")
    public ResponseEntity<ApiResponse> deleteUserProfileImage(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = userService.deleteUserProfileImage(currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "개인 작성 이력 조회 (게시물)")
    @GetMapping("/user/posts")
    public ResponseEntity<List<PostDTO.PostResponse>> getPostsByUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<PostDTO.PostResponse> response = null;
        if (currentUser == null) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            response = postService.getPostsByUser(currentUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "개인 작성 이력 조회 (댓글)")
    @GetMapping("/user/comments")
    public ResponseEntity<List<CommentDTO.CommentInfo>> getCommentsByUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<CommentDTO.CommentInfo> response = null;

        if (currentUser == null) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            response = commentService.getCommentByUser(currentUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    @ApiOperation(value = "회원 검색")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO.UserSimpleInfo>> searchUser(@RequestParam(value = "id", required = false, defaultValue = "") String id,
                                                                   @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                                   @ApiIgnore @AuthenticationPrincipal User currentUser) {
        List<UserDTO.UserSimpleInfo> response = userService.searchUser(id, name);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}