package com.tts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tts.entity.VoiceConfig;
import com.tts.mapper.VoiceConfigMapper;
import com.tts.service.IVoiceConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 语音配置服务实现类
 * 
 * @author TTS System
 */
@Service
public class VoiceConfigServiceImpl extends ServiceImpl<VoiceConfigMapper, VoiceConfig> implements IVoiceConfigService {

    @Override
    public List<VoiceConfig> getAllVoices() {
        return this.list();
    }
}
