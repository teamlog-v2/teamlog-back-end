package com.test.teamlog.global.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CompactOAuth2User {
    private String identification;
    private String profileImgUrl;
    private String name;
    private String authType;
}
