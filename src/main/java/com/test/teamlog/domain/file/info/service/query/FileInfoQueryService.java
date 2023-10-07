package com.test.teamlog.domain.file.info.service.query;

import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.file.info.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class FileInfoQueryService {
    private final FileInfoRepository fileInfoRepository;

    public FileInfo findByStoredFileName(String storedFileName) {
        return fileInfoRepository.findByStoredFileName(storedFileName).orElseThrow(() -> new NotFoundException("파일이 존재하지 않습니다."));
    }
}
