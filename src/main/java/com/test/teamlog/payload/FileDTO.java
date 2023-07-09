package com.test.teamlog.payload;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;


public class FileDTO {
    @Data
    @Builder
    public static class FileResourceInfo {
        private String contentType;
        private String fileName;
        private Resource resource;
    }
}
