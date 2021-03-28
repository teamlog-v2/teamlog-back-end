package com.test.teamlog.payload.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String id;
    private String name;
    private String introduction;
    private String profileImgPath;
}
