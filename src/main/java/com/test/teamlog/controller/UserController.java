package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.PostDTO;
import com.test.teamlog.payload.Token;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.security.JwtUtil;
import com.test.teamlog.service.UserService;
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
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @ApiOperation(value = "로그인 된 사용자인지 검증")
    @GetMapping("/validate")
    public ResponseEntity<UserDTO.UserSimpleInfo> validateUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        return new ResponseEntity<>(new UserDTO.UserSimpleInfo(currentUser), HttpStatus.OK);
    }

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

//    @ApiOperation(value = "로그아웃")
//    @GetMapping("/sign-out")
//    public ResponseEntity<ApiResponse> signOut(HttpServletRequest req, HttpServletResponse res) {
//        Cookie accessToken = cookieUtil.createEmptyCookie(JwtUtil.ACCESS_TOKEN_NAME);
//        res.addCookie(accessToken);
//        return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "로그아웃 성공"), HttpStatus.OK);
//    }

    @ApiOperation(value = "회원 조회")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO.UserResponse> getUserById(@PathVariable("id") String id,
                                                            @ApiIgnore @AuthenticationPrincipal User currentUser) {
        UserDTO.UserResponse userResponse = userService.getUser(id, currentUser);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 가입")
    @PostMapping("/users")
    public ResponseEntity<UserDTO.UserResponse> signUp(@Valid @RequestBody UserDTO.UserRequest userRequest) {
        UserDTO.UserResponse userResponse = userService.signUp(userRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    //Update
    @ApiOperation(value = "회원 수정")
    @PutMapping("/users")
    public ResponseEntity<ApiResponse> updateUser(@RequestPart(value = "key", required = true) UserDTO.UserUpdateRequest userRequest,
                                                  @RequestPart(value = "profileImg", required = false) MultipartFile image,
                                                  @ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = userService.updateUser(userRequest, image, currentUser);
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

    // Delete
    @ApiOperation(value = "회원 삭제")
    @DeleteMapping("/users")
    public ResponseEntity<ApiResponse> deleteUser(@ApiIgnore @AuthenticationPrincipal User currentUser) {
        ApiResponse apiResponse = userService.deleteUser(currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}