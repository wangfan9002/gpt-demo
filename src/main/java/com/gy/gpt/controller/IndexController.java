package com.gy.gpt.controller;


import com.gy.gpt.api.AskRequest;
import com.gy.gpt.common.Result;
import com.gy.gpt.service.FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Tag(name = "API管理", description = "API相关接口")
@RestController
@RequestMapping("/api/")
public class IndexController {
    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/ask")
    public Result<Object> ask(@RequestBody AskRequest request) {
        log.info("ask request:{}", request);
        String fileName = request.getFileName();
        if (StringUtils.isNotEmpty(fileName)) {
            Path filePath = Paths.get(fileUploadService.getProperties().getLocation()).resolve(fileName);
            try {
                String fileContent = FileUtils.readFileToString(filePath.toFile(), StandardCharsets.UTF_8);
                log.info("fileContent:{}", fileContent);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        return Result.success("请求成功");
    }
}
