package com.tts.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 自定义音色实体类
 * 
 * @author TTS System
 */
@Data
@TableName("custom_voice")
public class CustomVoice {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户 ID
     */
    private Long userId;

    /**
     * 是否公开（0-私有，1-公开）
     */
    private Integer isPublic;

    /**
     * 音色名称（用户自定义）
     */
    private String voiceName;

    /**
     * 原始音频样本路径
     */
    private String sampleAudioUrl;

    /**
     * 训练后的模型文件路径
     */
    private String modelPath;

    /**
     * 训练状态（0-待训练，1-训练中，2-已完成，3-失败）
     */
    private Integer trainingStatus;

    /**
     * 样本音频时长（秒）
     */
    private Integer sampleDuration;

    /**
     * 样本音频对应的文本（可选）
     */
    private String sampleText;

    /**
     * 音色描述
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

}
