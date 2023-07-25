package com.test.teamlog.domain.token.service;

import com.test.teamlog.domain.token.dto.CreateTokenResult;
import com.test.teamlog.entity.Token;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.global.security.JwtTokenProvider;
import com.test.teamlog.repository.TokenRepository;
import com.test.teamlog.service.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;

    public TokenService(JwtTokenProvider jwtTokenProvider,
                        TokenRepository tokenRepository,
                        CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepository = tokenRepository;
        this.userDetailsService = userDetailsService;
    }

    public CreateTokenResult createToken(String identification) {
        final String accessToken = createAccessToken(identification);
        final String refreshToken = createRefreshToken(identification);

        final Token token = tokenRepository.findByIdentification(identification).orElse(null);

        if (token != null) {
            token.updateAccessToken(accessToken);
            token.updateRefreshToken(refreshToken);
        } else {
            tokenRepository.save(
                    Token.builder()
                            .identification(identification)
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build()
            );
        }

        return new CreateTokenResult(accessToken, refreshToken);
    }

    @Transactional
    public String reissue(String refreshToken) {
        final String userId = jwtTokenProvider.getUserId(refreshToken);

        final Token token = tokenRepository.findByIdentification(userId).orElse(null);
        if (jwtTokenProvider.isTokenExpired(refreshToken) ||
                token == null ||
                !token.getRefreshToken().equals(refreshToken)) {
            throw new ResourceNotFoundException("토큰이 유효하지 않습니다");
        }

        final String accessToken = createAccessToken(userId);
        token.updateAccessToken(accessToken);

        return accessToken;
    }

    private String createAccessToken(String identification) {
        return jwtTokenProvider.generateAccessToken(identification);
    }

    private String createRefreshToken(String identification) {
        return jwtTokenProvider.generateRefreshToken(identification);
    }
}
