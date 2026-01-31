package com.tts.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * TTS 生成请求 DTO
 * 
 * @author TTS System
 */
@Data
public class TtsRequestDTO {

    /**
     * 要转换的文本
     */
    @NotBlank(message = "文本内容不能为空")
    private String text;

    /**
     * 语音角色名称（预设语音或自定义音色名称）
     */
    @NotBlank(message = "语音角色不能为空")
    private String voiceName;

    /**
     * 语速（默认 +0%）
     */
    private String rate = "+0%";

    /**
     * 音调（默认 +0Hz）
     */
    private String pitch = "+0Hz";

    /**
     * 是否使用自定义音色（true-自定义，false-预设）
     */
    private Boolean useCustomVoice = false;
}
