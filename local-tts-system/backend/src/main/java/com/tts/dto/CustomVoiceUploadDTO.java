package com.tts.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 自定义音色上传请求 DTO
 * 
 * @author TTS System
 */
@Data
public class CustomVoiceUploadDTO {

    /**
     * 音色名称
     */
    @NotBlank(message = "音色名称不能为空")
    private String voiceName;

    /**
     * 音频文件
     */
    @NotNull(message = "音频文件不能为空")
    private MultipartFile audioFile;

    /**
     * 音色描述
     */
    private String description;

    /**
     * 样本音频对应的文本（可选，用于优化训练）
     */
    private String sampleText;
}
