package com.test.teamlog.domain.postmedia.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;


// FIXME: 미디어쪽 작업 시 이름 변경 필요
public class FileDTO {
    @Data
    @Builder
    public static class FileResourceInfo {
        private String contentType;
        private String fileName;
        private Resource resource;
    }
}
