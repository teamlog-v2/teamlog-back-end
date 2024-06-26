package com.app.teamlog.domain.post.controller;

import com.app.teamlog.domain.post.dto.PostHistoryResponse;
import com.app.teamlog.domain.post.service.PostHistoryService;
import com.app.teamlog.global.security.AccountAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Tag(name = "게시물 히스토리 관리")
public class PostHistoryApiController {
    private final PostHistoryService postService;
    @Operation(summary = "게시물 수정 내역 조회")
    @GetMapping("/{id}/historys")
    public ResponseEntity<List<PostHistoryResponse>> readPostUpdateHistory(@PathVariable("id") long id, @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        final List<PostHistoryResponse> responseList = postService.readPostUpdateHistory(id, currentAccount.getAccount());

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
