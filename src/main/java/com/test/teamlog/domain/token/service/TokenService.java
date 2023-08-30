package com.test.teamlog.domain.token.service;

import com.test.teamlog.domain.token.dto.CreateTokenResult;
import com.test.teamlog.domain.token.dto.ReIssueResult;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.global.security.JwtTokenProvider;
import com.test.teamlog.domain.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    public CreateTokenResult createToken(String identification) {
        final String accessToken = createAccessToken(identification);
        final String refreshToken = createRefreshToken(identification);

        return new CreateTokenResult(accessToken, refreshToken);
    }

    // TODO: refresh token이 이상한 경우 refresh token만 무효화 하는 중. OAuth2.0 도입 후 개선 방안 생각 해보기
    public ReIssueResult reissue(String token) {
        try {
            if (token == null || jwtTokenProvider.isTokenExpired(token)) {
                throw new ResourceNotFoundException("유효하지 않은 토큰입니다.");
            }

            final String userId = jwtTokenProvider.getUserId(token);
            final String savedRefreshToken = jwtTokenProvider.getRefreshToken(userId);

            if (!token.equals(savedRefreshToken)) {
                jwtTokenProvider.invalidateToken(token);
                throw new ResourceNotFoundException("토큰이 유효하지 않습니다");
            }

            final String accessToken = createAccessToken(userId);
            final String refreshToken = createRefreshToken(userId);

            return new ReIssueResult(accessToken, refreshToken);
        } catch (Exception e) {
            log.warn("토큰 재발급 실패. message={}", e.getMessage(), e);
            throw new InvalidParameterException("토큰 재발급 실패");
        }
    }

    private String createAccessToken(String identification) {
        return jwtTokenProvider.generateAccessToken(identification);
    }

    private String createRefreshToken(String identification) {
        return jwtTokenProvider.generateRefreshToken(identification);
    }
}
