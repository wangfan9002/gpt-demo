package com.gy.gpt.dto;

import lombok.Builder;
import lombok.Data;

/**
 * "originalName": "temp.txt",
 * "storedName": "250815-2b03711ff0954bf3855688350e576bc6.txt",
 * 		"size": 6803,
 * 		"downloadUrl": "/api/file/download/250815-2b03711ff0954bf3855688350e576bc6.txt"
 */
@Data
@Builder
public class FileUploadResult {
    private String originalName;
    private String fileName;
    private long size;
    private String downloadUrl;
}