package com.test.teamlog.domain.file.info.service.query;

import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.file.info.repository.FileInfoRepository;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileInfoQueryService {
    private final FileInfoRepository fileInfoRepository;

    public FileInfo findByStoredFileName(String storedFileName) {
        return fileInfoRepository.findByStoredFileName(storedFileName).orElseThrow(() -> new ResourceNotFoundException("파일이 존재하지 않습니다."));
    }
}
