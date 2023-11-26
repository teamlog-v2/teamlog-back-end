package com.test.teamlog.domain.file.info.repository;

import com.test.teamlog.domain.file.info.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    Optional<FileInfo> findByStoredFileName(String storedFileName);
}
