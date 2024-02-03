package com.test.teamlog.domain.file.info.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String contentType;

    private String originalFileName;

    private String storedFileName;

    @Column(nullable = false)
    private String storedFilePath;

    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

    public static FileInfo create(String contentType,
                                  String originalFileName,
                                  String storedFileName,
                                  String storedFilePath) {
        return FileInfo.builder()
                .contentType(contentType)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .storedFilePath(storedFilePath)
                .build();
    }
}
