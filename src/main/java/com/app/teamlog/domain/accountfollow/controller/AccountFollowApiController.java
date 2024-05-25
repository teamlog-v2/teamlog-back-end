package com.app.teamlog.domain.accountfollow.controller;

import com.app.teamlog.domain.accountfollow.dto.AccountFollowerReadResponse;
import com.app.teamlog.domain.accountfollow.dto.AccountFollowerReadResult;
import com.app.teamlog.domain.accountfollow.dto.AccountFollowingReadResponse;
import com.app.teamlog.domain.accountfollow.dto.AccountFollowingReadResult;
import com.app.teamlog.domain.accountfollow.service.AccountFollowService;
import com.app.teamlog.global.security.AccountAdapter;
import com.app.teamlog.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "유저 팔로우 관리")
public class AccountFollowApiController {
    private final AccountFollowService accountFollowService;

    @Operation(summary = "유저 팔로우")
    @PostMapping("/accountfollows/{targetId}")
    public ResponseEntity<ApiResponse> follow(@PathVariable("targetId") String toaccountId,
                                              @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        ApiResponse apiResponse = accountFollowService.follow(currentAccount.getAccount(), toaccountId);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "유저 언팔로우")
    @DeleteMapping("/accountfollows/{targetId}")
    public ResponseEntity<ApiResponse> unfollow(@PathVariable("targetId") String fromaccountId,
                                                @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        ApiResponse apiResponse = accountFollowService.unfollow(fromaccountId, currentAccount.getAccount());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "유저 팔로워 조회")
    @GetMapping("/accounts/{id}/follower")
    public ResponseEntity<List<AccountFollowerReadResponse>> readAllFollower(@PathVariable("id") String identification,
                                                                             @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        final List<AccountFollowerReadResult> resultList = accountFollowService.readAllFollower(identification, currentAccount.getAccount());
        final List<AccountFollowerReadResponse> responseList = resultList.stream().map(AccountFollowerReadResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(summary = "유저 팔로잉 조회")
    @GetMapping("/accounts/{id}/following")
    public ResponseEntity<List<AccountFollowingReadResponse>> readAllFollowing(@PathVariable("id") String id,
                                                                               @Parameter(hidden = true) @AuthenticationPrincipal AccountAdapter currentAccount) {
        final List<AccountFollowingReadResult> resultList = accountFollowService.readAllFollowing(id, currentAccount.getAccount());
        final List<AccountFollowingReadResponse> responseList = resultList.stream().map(AccountFollowingReadResponse::from).collect(Collectors.toList());

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
