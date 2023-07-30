package com.test.teamlog.global.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthenticationEntryPoint: ExceptionTranslationManager에서 사용
 * ExceptionTranslationManager: 필터 체인에서 투척된 모든 AccessDeniedException 및 AuthenticationException 처리
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
            throws IOException {
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "엑세스 권한 없음");
    }
}