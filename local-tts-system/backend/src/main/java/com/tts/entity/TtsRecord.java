package com.tts.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * TTS 生成记录实体类
 * 
 * @author TTS System
 */
@Data
@TableName("tts_record")
public class TtsRecord {

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
     * 原始文本内容
     */
    private String textContent;

    /**
     * 语音角色名称
     */
    private String voiceName;

    /**
     * 语速（如 +0%）
     */
    private String rate;

    /**
     * 音调（如 +0Hz）
     */
    private String pitch;

    /**
     * 音频文件路径
     */
    private String audioUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 音频时长（秒）
     */
    private Integer duration;

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
     * 逻辑删除（0-正常，1-删除）
     */
    @TableLogic
    private Integer isDeleted;
}
