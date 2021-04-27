package com.test.teamlog.payload;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

public class UserDTO {
    @Data
    public static class UserRequest {
        @NotBlank(message = "올바른 값 입력해주세요")
        private String id;
        @NotBlank(message = "올바른 값 입력해주세요")
        private String password;
        @NotBlank(message = "올바른 값 입력해주세요")
        private String name;
        private String introduction;
        private String profileImgPath;
    }

    @Data
    @Builder
    public static class UserResponse {
        private String id;
        private String name;
        private String introduction;
        private String profileImgPath;
    }

    @Data
    @Builder
    public static class UserSimpleInfo {
        private String id;
        private String name;
        private String profileImgPath;
    }

}
