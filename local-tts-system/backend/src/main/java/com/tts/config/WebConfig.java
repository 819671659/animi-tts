package com.tts.config;

import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web 访问配置，实现物理路径映射
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${tts.audio-base-path}")
    private String audioBasePath;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 确保路径以 / 结尾
        String path = audioBasePath.endsWith("/") ? audioBasePath : audioBasePath + "/";

        // 映射 F:/.../tts-storage/ 到 http://.../api/audios/**
        registry.addResourceHandler("/api/audios/**")
                .addResourceLocations("file:" + path);

        // 自动创建必要的子目录
        createDirectories(path);
    }

    private void createDirectories(String basePath) {
        String[] dirs = { "samples", "records" };
        for (String dir : dirs) {
            File folder = new File(basePath + dir);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
    }
}
