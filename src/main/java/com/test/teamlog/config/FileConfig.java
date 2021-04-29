package com.test.teamlog.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ConfigurationProperties(prefix = "file")
public class FileConfig {
    private String uploadDir;
}