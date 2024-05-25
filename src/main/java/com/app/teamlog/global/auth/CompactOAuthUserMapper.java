package com.app.teamlog.global.auth;

import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.core.user.OAuth2User;

@UtilityClass
public class CompactOAuthUserMapper {
    // FIXME: authType과 함께 개선 포인트
    public CompactOAuth2User convert(OAuth2User oAuth2User, String authType) {
        switch (authType) {
            case "github":
                return CompactOAuth2User.builder()
                        .identification(oAuth2User.getAttribute("login"))
                        .name(oAuth2User.getAttribute("login"))
                        .profileImgUrl(oAuth2User.getAttribute("avatar_url"))
                        .authType(authType)
                        .build();
            default:
                throw new IllegalArgumentException("유효하지 않는 authType 입니다.");
        }
    }
}
