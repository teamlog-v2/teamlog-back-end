package com.test.teamlog.global.auth;

import com.test.teamlog.global.security.JwtTokenProvider;
import com.test.teamlog.global.security.AccountAdapter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${cookie.domain}")
    private String cookieDomain;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            log.warn("Response has already been committed.");
            return;
        }

        final AccountAdapter principal = (AccountAdapter) authentication.getPrincipal();

        // 여기서 authentication 설정하고
        // 이 서버 전용 accessToken 설정하기
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseCookie cookie = ResponseCookie.from("Refresh-Token", jwtTokenProvider.generateRefreshToken(principal.getUsername()))
                .domain(cookieDomain)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());

        getRedirectStrategy().sendRedirect(request, response, makeRedirectUrl(principal.getUsername()));
    }

    private String makeRedirectUrl(String identification) {
        final String url = "http://localhost:3000/login";
        final String accessToken = jwtTokenProvider.generateAccessToken(identification);

        return UriComponentsBuilder.fromUriString(url)
                .queryParam("AccessToken", accessToken)
                .build().toUriString();
    }
}
