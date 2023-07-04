package com.test.teamlog.domain.token.service;

import com.test.teamlog.domain.token.dto.CreateTokenResult;
import com.test.teamlog.entity.Token;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.global.security.JwtComponent;
import com.test.teamlog.repository.TokenRepository;
import com.test.teamlog.service.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {
    private final JwtComponent jwtComponent;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;

    public TokenService(JwtComponent jwtComponent,
                        TokenRepository tokenRepository,
                        CustomUserDetailsService userDetailsService) {
        this.jwtComponent = jwtComponent;
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
        final String userId = jwtComponent.getUserId(refreshToken);

        final Token token = tokenRepository.findByIdentification(userId).orElse(null);
        if (!jwtComponent.validateToken(refreshToken, userDetailsService.loadUserByUsername(userId)) ||
                token == null ||
                !token.getRefreshToken().equals(refreshToken)) {
            throw new ResourceNotFoundException("토큰이 유효하지 않습니다");
        }

        final String accessToken = createAccessToken(userId);
        token.updateAccessToken(accessToken);

        return accessToken;
    }

    private String createAccessToken(String identification) {
        return jwtComponent.generateAccessToken(identification);
    }

    private String createRefreshToken(String identification) {
        return jwtComponent.generateRefreshToken(identification);
    }
}
