package com.app.teamlog.domain.file.management.controller;

import com.app.teamlog.domain.file.management.dto.FileResourceInfo;
import com.app.teamlog.domain.file.management.service.FileManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileManagementController {
    private final FileManagementService fileManagementService;

    @GetMapping("/download/{storedFileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String storedFileName) {
        try {
            FileResourceInfo fileResourceInfo = fileManagementService.downloadFile(storedFileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileResourceInfo.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResourceInfo.getFileName() + "\"")
                    .body(fileResourceInfo.getResource());
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            log.error("파일 다운로드 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
