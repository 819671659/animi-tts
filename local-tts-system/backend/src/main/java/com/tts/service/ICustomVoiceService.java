package com.tts.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tts.entity.CustomVoice;
import org.springframework.web.multipart.MultipartFile;

/**
 * 自定义音色服务接口
 * 
 * @author TTS System
 */
public interface ICustomVoiceService extends IService<CustomVoice> {

    /**
     * 上传音色样本
     *
     * @param userId      用户 ID
     * @param voiceName   音色名称
     * @param audioFile   音频文件
     * @param description 描述
     * @param sampleText  样本文本（可选）
     * @return 自定义音色实体
     */
    CustomVoice uploadVoiceSample(Long userId, String voiceName, MultipartFile audioFile, String description,
            String sampleText);

    /**
     * 训练音色模型（异步）
     *
     * @param id 音色 ID
     */
    void trainVoiceModel(Long id);

    /**
     * 分页获取指定用户的自定义音色列表
     *
     * @param userId 用户 ID
     * @param page   页码
     * @param size   每页大小
     * @return 分页数据
     */
    IPage<CustomVoice> getCustomVoicesByUserId(Long userId, Integer page, Integer size);

    /**
     * 获取公开分享的音色列表
     */
    java.util.List<CustomVoice> getPublicVoices();

    /**
     * 更新分享状态
     */
    void updateShareStatus(Long id, Long userId, Integer isPublic);
}
