package com.test.teamlog.domain.file.info.service.command;

import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.file.info.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileInfoCommandService {
    private final FileInfoRepository fileInfoRepository;

    public FileInfo save(FileInfo fileInfo) {
        if (fileInfo == null) return null;

        return fileInfoRepository.save(fileInfo);
    }
}
