package com.app.teamlog.global.exception.handler;

import com.app.teamlog.global.dto.ApiResponse;
import com.app.teamlog.global.exception.BadRequestException;
import com.app.teamlog.global.exception.ResourceAlreadyExistsException;
import com.app.teamlog.global.exception.ResourceForbiddenException;
import com.app.teamlog.global.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resolveException(ResourceNotFoundException e) {
        log.warn("ResourceNotFoundException: {}", e.getMessage());
        return new ResponseEntity<>(new ApiResponse(false), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceForbiddenException.class)
    public ResponseEntity<ApiResponse> resolveException(ResourceForbiddenException e) {
        log.warn("ResourceForbiddenException: {}", e.getMessage());
        return new ResponseEntity<>(new ApiResponse(false), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> resolveException(ResourceAlreadyExistsException e) {
        log.warn("ResourceAlreadyExistsException: {}", e.getMessage());
        return new ResponseEntity<>(new ApiResponse(false), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> resolveException(BadRequestException e) {
        log.warn("BadRequestException: {}", e.getMessage());
        return new ResponseEntity<>(new ApiResponse(false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        log.warn("MethodArgumentNotValidException: {}", errorMessage);
        return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, errorMessage), HttpStatus.BAD_REQUEST);
    }
}