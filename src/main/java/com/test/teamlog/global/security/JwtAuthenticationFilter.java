package com.test.teamlog.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String SIGN_IN_ENDPOINT = "/api/accounts/sign-in";
    private final String SIGN_UP_ENDPOINT = "/api/accounts/sign-up";
    private final String REISSUE_ENDPOINT = "/api/tokens/reissue";
    private final String FILE_DOWNLOAD_ENDPOINT = "/api/files/download";
    private final List<String> EXCLUDE_ENDPOINTS = List.of(SIGN_IN_ENDPOINT, SIGN_UP_ENDPOINT, REISSUE_ENDPOINT, FILE_DOWNLOAD_ENDPOINT);

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        // 로그인 여부와 관계 없이 동작하되 권한에 따라 다르게 동작하는 경우가 있다.
        if (authorization != null) {
            final String jwtToken = jwtTokenProvider.parseToken(authorization);
            final Authentication authentication = jwtTokenProvider.authenticate(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDE_ENDPOINTS.stream().anyMatch(endpoint -> request.getServletPath().startsWith(endpoint));
    }
}