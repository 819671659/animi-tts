package com.tts.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tts.entity.EmailCodeVerify;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮箱验证码 Mapper 接口
 * 
 * @author TTS System
 */
@Mapper
public interface EmailCodeVerifyMapper extends BaseMapper<EmailCodeVerify> {
}
