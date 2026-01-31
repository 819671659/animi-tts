package com.tts.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tts.entity.CustomVoice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 自定义音色 Mapper
 * 
 * @author TTS System
 */
@Mapper
public interface CustomVoiceMapper extends BaseMapper<CustomVoice> {
}
