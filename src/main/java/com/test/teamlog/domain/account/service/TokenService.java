package com.test.teamlog.domain.account.service;

import com.test.teamlog.global.security.JwtComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtComponent jwtComponent;

    public String createAccessToken(String identification) {
        return jwtComponent.generateAccessToken(identification);
    }

    public String createRefreshToken(String identification) {
        return jwtComponent.generateRefreshToken(identification);
    }
}
