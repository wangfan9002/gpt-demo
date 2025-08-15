package com.gy.gpt.controller;


import com.gy.gpt.common.Result;
import com.gy.gpt.dto.FileUploadResult;
import com.gy.gpt.exception.BusinessException;
import com.gy.gpt.exception.ErrorCode;
import com.gy.gpt.service.FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Tag(name = "文件管理", description = "文件相关接口")
@RestController
@RequestMapping("/api/")
public class FileController {

    // 条件查询
    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/file/upload")
    public Result<FileUploadResult> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("uploadFile");
        FileUploadResult result = fileUploadService.uploadFile(file);
        return Result.success(result);
    }

    @GetMapping("/file/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(fileUploadService.getProperties().getLocation()).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR.getCode(), "文件不存在或不可读");
            }
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR.getCode(), "文件下载失败: " + e.getMessage());
        }
    }

    // 在FileUploadService中添加方法
    private void validateFileContent(MultipartFile file, String expectedExtension) {
        try (InputStream is = file.getInputStream()) {
            // 简单的文件头校验
            byte[] header = new byte[4];
            is.read(header);

            switch (expectedExtension.toLowerCase()) {
                case "txt":
                case "pdf":
                    // PDF文件头校验
                    if ("%PDF".equals(new String(header, 0, 4))) {
                        return;
                    }
                    break;
                case "jpg":
                case "jpeg":
                    // JPEG文件头校验 (FF D8 FF E0)
                    if ((header[0] & 0xFF) == 0xFF &&
                            (header[1] & 0xFF) == 0xD8 &&
                            (header[2] & 0xFF) == 0xFF) {
                        return;
                    }
                    break;
                case "png":
                    // PNG文件头校验 (89 50 4E 47)
                    if (header[0] == -119 &&
                            header[1] == 80 &&
                            header[2] == 78 &&
                            header[3] == 71) {
                        return;
                    }
                    break;
            }

            throw new BusinessException("文件内容与扩展名不符");
        } catch (IOException e) {
            throw new BusinessException("文件校验失败");
        }
    }
}
