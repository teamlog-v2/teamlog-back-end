package com.test.teamlog.payload;

import com.test.teamlog.domain.account.model.User;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;


public class FileDTO {
    @Data
    @Builder
    public static class FileInfo {
        private Long id;
        private String contentType;
        private String fileName;
        private String fileDownloadUri;
    }

    @Data
    @Builder
    public static class FileResourceInfo {
        private String contentType;
        private String fileName;
        private Resource resource;
    }
}
