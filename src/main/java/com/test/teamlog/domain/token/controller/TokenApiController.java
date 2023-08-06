package com.test.teamlog.domain.token.controller;

import com.test.teamlog.domain.token.dto.ReIssueResponse;
import com.test.teamlog.domain.token.dto.ReIssueResult;
import com.test.teamlog.domain.token.service.TokenService;
import com.test.teamlog.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tokens")
public class TokenApiController {
    @Value("${cookie.domain}")
    private String cookieDomain;
    private final TokenService tokenService;

    @Operation(summary = "리프레시 토큰 발급")
    @PostMapping("/reissue")
    public ResponseEntity<ReIssueResponse> refresh(@CookieValue(value = "Refresh-Token") String refreshToken,
                                                   HttpServletResponse httpServletResponse) {
        try {
            final ReIssueResult result = tokenService.reissue(refreshToken);
            ResponseCookie cookie = ResponseCookie.from("Refresh-Token", result.getRefreshToken())
                    .domain(cookieDomain)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .build();
            httpServletResponse.addHeader(SET_COOKIE, cookie.toString());

            return new ResponseEntity<>(ReIssueResponse.of(result), HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
