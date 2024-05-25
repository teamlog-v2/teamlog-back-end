package com.app.teamlog.domain.file.management.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@RequiredArgsConstructor
public class FileResourceInfo {
    private String contentType;
    private String fileName;
    private Resource resource;

    public static FileResourceInfo of(String contentType, String fileName, Resource resource) {
        FileResourceInfo fileResourceInfo = new FileResourceInfo();
        fileResourceInfo.contentType = contentType;
        fileResourceInfo.fileName = fileName;
        fileResourceInfo.resource = resource;

        return fileResourceInfo;
    }
}
