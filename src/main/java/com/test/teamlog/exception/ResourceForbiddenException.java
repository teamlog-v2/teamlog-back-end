package com.test.teamlog.exception;

import com.test.teamlog.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ResourceForbiddenException extends RuntimeException{

    private ApiResponse apiResponse;

    private String forbiddenResourceName;
    private String userId;

    public ResourceForbiddenException(String forbiddenResourceName, String userId) {
        super();
        this.forbiddenResourceName = forbiddenResourceName;
        this.userId = userId;
        setApiResponse();
    }

    public ApiResponse getApiResponse() {
        return apiResponse;
    }

    private void setApiResponse() {
        String message = String.format("%s is forbbiden for User : '%s'", forbiddenResourceName, userId);
        apiResponse = new ApiResponse(Boolean.FALSE, message);
    }
}
