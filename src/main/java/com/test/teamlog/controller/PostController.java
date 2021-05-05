package com.test.teamlog.controller;

import com.test.teamlog.entity.PostTag;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.PagedResponse;
import com.test.teamlog.payload.PostDTO;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.service.PostService;
import com.test.teamlog.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {
    private final PostService postService;

    @ApiOperation(value = "단일 포스트 조회")
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO.PostResponse> getPostById(@PathVariable("id") long id) {
        PostDTO.PostResponse response = postService.getPost(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(31536000, TimeUnit.SECONDS))
                .body(response);
    }

    @ApiOperation(value = "모든 포스트 조회")
    @GetMapping
    public ResponseEntity<PagedResponse<PostDTO.PostResponse>> getAllPosts(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                           @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        PagedResponse<PostDTO.PostResponse> response = postService.getAllPosts(page, size);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(31536000, TimeUnit.SECONDS))
                .body(response);
    }

    @ApiOperation(value = "프로젝트의 모든 포스트 조회")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<PagedResponse<PostDTO.PostResponse>> getPostsByProject(@PathVariable("projectId") long projectId,
                                                                        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                        @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        PagedResponse<PostDTO.PostResponse> response = postService.getPostsByProject(projectId,page,size);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(31536000, TimeUnit.SECONDS))
                .body(response);
    }

    @ApiOperation(value = "위치정보가 있는 Public 포스트들 조회")
    @GetMapping("/with-location")
    public ResponseEntity<List<PostDTO.PostResponse>> getLocationPosts() {
        List<PostDTO.PostResponse> response = postService.getLocationPosts();
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(31536000, TimeUnit.SECONDS))
                .body(response);
    }

    @ApiOperation(value = "해시태그 선별 조회")
    @GetMapping("/project/{projectId}/hashtag/{names}")
    public ResponseEntity<PagedResponse<PostDTO.PostResponse>> getPostByTag(@PathVariable("projectId") long projectId,
                                                                   @PathVariable("names") String[] names,
                                                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        long start = System.currentTimeMillis();
        List<String> hashtags = Arrays.asList(names);
        PagedResponse<PostDTO.PostResponse> response = postService.getPostsInProjectByHashTag(projectId, hashtags, page, size);
        long end = System.currentTimeMillis();
        System.out.println("수행시간: " + (end - start) + " ms");
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(31536000, TimeUnit.SECONDS))
                .body(response);
    }

    @ApiOperation(value = "프로젝트 내 게시물 검색")
    @GetMapping("/project/{projectId}/{keyword}")
    public ResponseEntity<PagedResponse<PostDTO.PostResponse>> getPostByTag(@PathVariable("projectId") long projectId,
                                                                   @PathVariable("keyword") String keyword,
                                                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        PagedResponse<PostDTO.PostResponse> response = postService.searchPostsInProject(projectId, keyword, page, size);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(31536000, TimeUnit.SECONDS))
                .body(response);
    }

    @ApiOperation(value = "포스트 생성")
    @PostMapping
    public ResponseEntity<ApiResponse> createProject(@RequestPart(value = "key", required = true) PostDTO.PostRequest request,
                                                     @RequestPart(value = "media", required = false) MultipartFile[] media,
                                                     @RequestPart(value = "files", required = false) MultipartFile[] files) {
        ApiResponse apiResponse = postService.createPost(request, media, files);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "포스트 수정")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProject(@PathVariable("id") long id,
                                                     @RequestBody PostDTO.PostRequest request) {
        ApiResponse apiResponse = postService.updatePost(id, request);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "포스트 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id) {
        ApiResponse apiResponse = postService.deletePost(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}