package com.tts.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tts.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 * 
 * @author TTS System
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
