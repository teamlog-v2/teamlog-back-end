package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

            final FileInfo profileImage = user.getProfileImage();
            if (profileImage != null) this.profileImgPath = profileImage.getStoredFilePath();
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
