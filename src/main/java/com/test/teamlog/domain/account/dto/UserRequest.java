package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.User;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotBlank;

public class UserRequest {



    @Data
    public static class UserUpdateRequest {
        private String id;
        private String password;
        @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
        private String name;
        private String introduction;
        private Boolean defaultImage;
    }

    @Data
    public static class UserResponse {
        private Boolean isMe;
        private Boolean isFollow;
        private String identification;
        private String name;
        private String introduction;
        private String profileImgPath;
        public UserResponse(User user) {
            this.identification = user.getIdentification();
            this.name = user.getName();
            this.introduction = user.getIntroduction();
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

    // FIXME: UserFollow 리팩할 때 삭제

    @Data
    @NoArgsConstructor
    public static class UserSimpleInfo {
        private String id;
        private String name;
        private String profileImgPath;

        public UserSimpleInfo(User user) {
            this.id = user.getIdentification();
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

    @Setter @Getter
    public static class UserFollowInfo extends UserSimpleInfo {
        private Boolean isFollow;
        public UserFollowInfo(User user) {
            super(user);
        }
    }
}
