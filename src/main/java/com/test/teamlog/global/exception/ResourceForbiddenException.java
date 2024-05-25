package com.test.teamlog.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 리소스 접근 권한이 없을 때 발생하는 예외
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ResourceForbiddenException extends RuntimeException {
    public ResourceForbiddenException(String message) {
        super(message);
    }
}
