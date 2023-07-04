package com.test.teamlog.domain.token.controller;

import com.test.teamlog.domain.token.dto.ReIssueResponse;
import com.test.teamlog.domain.token.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tokens")
public class TokenApiController {
    private final TokenService tokenService;

    @Operation(summary = "리프레시 토큰 발급")
    @PostMapping("/reissue")
    public ResponseEntity<ReIssueResponse> refresh(@CookieValue(value = "Refresh-Token") String refreshToken) {
        final String accessToken = tokenService.reissue(refreshToken);
        return new ResponseEntity<>(new ReIssueResponse(accessToken), HttpStatus.OK);
    }
}
