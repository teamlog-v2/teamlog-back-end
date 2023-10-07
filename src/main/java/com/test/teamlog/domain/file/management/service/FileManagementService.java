package com.test.teamlog.domain.file.management.service;

import com.test.teamlog.domain.file.config.FileConfig;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.file.info.service.command.FileInfoCommandService;
import com.test.teamlog.domain.file.info.service.query.FileInfoQueryService;
import com.test.teamlog.domain.file.management.dto.FileResourceInfo;
import com.test.teamlog.global.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.springframework.util.MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;


@Slf4j
@Service
public class FileManagementService {

    private final Path directory;
    private final String downloadUrlPrefix;

    private final FileInfoQueryService fileInfoQueryService;
    private final FileInfoCommandService fileInfoCommandService;

    public FileManagementService(FileInfoCommandService fileInfoCommandService,
                                 FileInfoQueryService fileInfoQueryService,
                                 FileConfig fileConfig) {
        this.fileInfoCommandService = fileInfoCommandService;
        this.fileInfoQueryService = fileInfoQueryService;

        this.directory = Path.of(fileConfig.getUploadDir());
        this.downloadUrlPrefix = fileConfig.getDownloadUrlPrefix();
    }

    public void uploadFile(MultipartFile file) throws IOException {
        checkValidation(file);

        // 경로 생성
        makeDirectoryIfNotExist(this.directory);

        // 파일 저장
        final String originalFileName = file.getOriginalFilename();
        final String storedFileName = makeStoredFileName(originalFileName);

        final Path filePath = Files.createFile(this.directory.resolve(storedFileName));
        file.transferTo(filePath);

        // 파일 정보 저장
        final FileInfo fileInfo = FileInfo.create(file.getContentType(), originalFileName, storedFileName, downloadUrlPrefix + "/" + storedFileName);
        fileInfoCommandService.save(fileInfo);
    }

    public FileResourceInfo downloadFile(String storedFileName) throws MalformedURLException, UnsupportedEncodingException {
        final FileInfo fileInfo = fileInfoQueryService.findByStoredFileName(storedFileName);

        String contentType = StringUtils.hasText(fileInfo.getContentType()) ? fileInfo.getContentType() : APPLICATION_OCTET_STREAM_VALUE;
        final String originalFileName = new String(fileInfo.getOriginalFileName().getBytes(StandardCharsets.ISO_8859_1), "euc-kr");

        Path filePath = directory.resolve(fileInfo.getStoredFileName());
        Resource resource = new UrlResource(filePath.toUri());

        return FileResourceInfo.of(contentType, originalFileName, resource);
    }

    private void makeDirectoryIfNotExist(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private void checkValidation(MultipartFile file) {
        if (file == null) {
            throw new BadRequestException("유효하지 않은 파일입니다.");
        }
    }

    private String makeStoredFileName(String originalFileName) {
        final String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        return UUID.randomUUID() + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + extension;
    }
}
