package com.tts.dto;

import lombok.Data;

/**
 * TTS 生成响应 DTO
 * 
 * @author TTS System
 */
@Data
public class TtsResponseDTO {

    /**
     * 音频文件下载 URL
     */
    private String audioUrl;

    /**
     * 记录 ID
     */
    private Long recordId;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 音频时长（秒）
     */
    private Integer duration;
}
