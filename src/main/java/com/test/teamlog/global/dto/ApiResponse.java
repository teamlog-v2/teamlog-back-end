package com.test.teamlog.global.dto;

import com.test.teamlog.domain.account.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private Boolean success;
    private String message;
}