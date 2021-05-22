package com.test.teamlog.exception;

import com.test.teamlog.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private ApiResponse apiResponse;

    public BadRequestException(String message) {
        super();
        setApiResponse(message);
    }

    public ApiResponse getApiResponse() {
        return apiResponse;
    }

    private void setApiResponse(String message) {
        apiResponse = new ApiResponse(Boolean.FALSE, message);
    }
}