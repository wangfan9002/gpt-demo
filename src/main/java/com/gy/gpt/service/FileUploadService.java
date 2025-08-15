package com.gy.gpt.service;

import com.gy.gpt.config.FileUploadProperties;
import com.gy.gpt.dto.FileUploadResult;
import com.gy.gpt.exception.BusinessException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {
    @Getter
    private final FileUploadProperties properties;

    public FileUploadResult uploadFile(MultipartFile file) {
        // 1. 检查文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 2. 检查文件大小
        long maxBytes = DataSize.parse(properties.getMaxSize()).toBytes();
        if (file.getSize() > maxBytes) {
            throw new BusinessException("文件大小不能超过 " + properties.getMaxSize());
        }

        // 3. 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        if (!properties.getAllowedExtensions().contains(fileExtension.toLowerCase())) {
            throw new BusinessException("不支持的文件类型，仅支持: " +
                    String.join(", ", properties.getAllowedExtensions()));
        }

        // 4. 创建存储目录
        File uploadDir = new File(properties.getLocation());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 5. 生成唯一文件名
        String uniqueFileName = generateUniqueFileName(originalFilename);
        log.info("originalFilename:{}, uniqueFileName:{}", originalFilename, uniqueFileName);
        Path filePath = Paths.get(properties.getLocation(), uniqueFileName);

        try {
            // 6. 保存文件
            file.transferTo(filePath);

            // 7. 返回结果
            return FileUploadResult.builder()
                    .originalName(originalFilename)
                    .fileName(uniqueFileName)
                    .size(file.getSize())
                    .downloadUrl("/api/file/download/" + uniqueFileName)
                    .build();
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String generateUniqueFileName(String originalFilename) {
        String dateStr = DateFormatUtils.format(new Date(), "yyMMdd");
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = getFileExtension(originalFilename);
        return dateStr + "-" + uuid + (extension.isEmpty() ? "" : "." + extension);
    }
}