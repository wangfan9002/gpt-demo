package com.gy.gpt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "file.upload")
@Data
public class FileUploadProperties {
    private List<String> allowedExtensions = new ArrayList<>(Collections.singletonList("txt"));
    private String maxSize;
    private String location;
}
