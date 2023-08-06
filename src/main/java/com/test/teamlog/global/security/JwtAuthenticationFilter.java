package com.test.teamlog.global.security;

import io.jsonwebtoken.ExpiredJwtException;
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
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String SIGN_IN_ENDPOINT = "/api/accounts/sign-in";
    private final String SIGN_UP_ENDPOINT = "/api/accounts/sign-up";
    private final String REISSUE_ENDPOINT = "/api/tokens/reissue";
    private final Set<String> EXCLUDE_ENDPOINTS = Set.of(SIGN_IN_ENDPOINT, SIGN_UP_ENDPOINT, REISSUE_ENDPOINT);

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // TODO: 프론트 파악 후 리팩토링
        String jwtToken = request.getHeader("Authorization");

        try {
            final Authentication authentication = jwtTokenProvider.authenticate(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {

        } catch (Exception e) {
            // 이렇게 하면 유효 기간이 지난 건지 아니면 토큰이 잘못된 건지 알 수 없다.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDE_ENDPOINTS.contains(request.getServletPath());
    }
}