package com.test.teamlog.payload;

import com.test.teamlog.entity.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public static class UserResponse {
        private String id;
        private String name;
        private String introduction;
        private String profileImgPath;
        public UserResponse(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.introduction = user.getIntroduction();
            String imgUri = null;
            if(user.getProfileImgPath() != null){
                imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/downloadFile/")
                        .path(user.getProfileImgPath())
                        .toUriString();
            }
            this.profileImgPath = imgUri;
        }
    }

    @Data
    public static class UserSimpleInfo {
        private String id;
        private String name;
        private String profileImgPath;

        public UserSimpleInfo(User user) {
            this.id = user.getId();
            this.name = user.getName();
            String imgUri = null;
            if(user.getProfileImgPath() != null){
                imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/resources/")
                        .path(user.getProfileImgPath())
                        .toUriString();
            }
            this.profileImgPath = imgUri;
        }
    }

}
