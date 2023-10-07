package com.test.teamlog.domain.file.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "file")
public class FileConfig {
    private String uploadDir;
    private String downloadUrlPrefix;
}