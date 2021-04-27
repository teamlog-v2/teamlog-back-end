package com.test.teamlog.payload;

import com.test.teamlog.entity.AccessModifier;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class ProjectDTO {
    @Getter
    public static class CreateRequest {
        private String name;
        private String introduction;
        private AccessModifier accessModifier;
    }

    @Setter
    public static class UpdateRequest {
        private String name;
        private String introduction;
        private AccessModifier accessModifier;
    }

    @Data
    @Builder
    public static class Response {
        private String name;
        private String introduction;
        private AccessModifier accessModifier;
        private String masterName;

    }

}
