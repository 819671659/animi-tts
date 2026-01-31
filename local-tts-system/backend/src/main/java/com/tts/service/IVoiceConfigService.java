package com.tts.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tts.entity.VoiceConfig;

import java.util.List;

/**
 * 语音配置服务接口
 * 
 * @author TTS System
 */
public interface IVoiceConfigService extends IService<VoiceConfig> {

    /**
     * 获取所有可用的语音配置（预设）
     *
     * @return 语音配置列表
     */
    List<VoiceConfig> getAllVoices();
}
