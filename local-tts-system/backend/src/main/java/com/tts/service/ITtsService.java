package com.tts.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tts.dto.TtsRequestDTO;
import com.tts.dto.TtsResponseDTO;
import com.tts.entity.TtsRecord;

/**
 * TTS 服务接口
 * 
 * @author TTS System
 */
public interface ITtsService extends IService<TtsRecord> {

    /**
     * 生成语音
     *
     * @param userId  用户 ID
     * @param request TTS 生成请求
     * @return TTS 响应数据
     */
    TtsResponseDTO generateTts(Long userId, TtsRequestDTO request);

    /**
     * 分页查询指定用户的历史记录
     *
     * @param userId 用户 ID
     * @param page   页码
     * @param size   每页大小
     * @return 分页数据
     */
    IPage<TtsRecord> getHistoryByUserId(Long userId, Integer page, Integer size);

    /**
     * 重命名记录
     */
    void renameRecord(Long id, Long userId, String newName);

    /**
     * 删除记录并删除物理文件
     */
    void deleteRecord(Long id, Long userId);
}
