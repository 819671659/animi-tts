package com.tts.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 语音配置预设实体类
 * 
 * @author TTS System
 */
@Data
@TableName("voice_config")
public class VoiceConfig {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置名称（如"标准女声"）
     */
    private String configName;

    /**
     * 语音角色名称
     */
    private String voiceName;

    /**
     * 语速
     */
    private String rate;

    /**
     * 音调
     */
    private String pitch;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
}
