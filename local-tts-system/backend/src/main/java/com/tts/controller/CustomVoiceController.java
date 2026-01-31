package com.tts.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tts.common.Result;
import com.tts.entity.CustomVoice;
import com.tts.service.ICustomVoiceService;
import com.tts.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 自定义音色控制器
 * 
 * @author TTS System
 */
@Slf4j
@RestController
@RequestMapping("/api/voice")
public class CustomVoiceController {

    @Autowired
    private ICustomVoiceService customVoiceService;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * 上传音色样本
     */
    @PostMapping("/upload")
    public Result<CustomVoice> uploadVoiceSample(
            @RequestParam("voiceName") String voiceName,
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sampleText", required = false) String sampleText) {

        Long userId = securityUtils.getUserId();
        log.info("收到音色上传请求，用户ID: {}, 音色名称: {}", userId, voiceName);

        CustomVoice customVoice = customVoiceService.uploadVoiceSample(userId, voiceName, audioFile, description,
                sampleText);

        return Result.success("音色样本上传成功", customVoice);
    }

    /**
     * 训练音色模型
     */
    @PostMapping("/train/{id}")
    public Result<Void> trainVoiceModel(@PathVariable Long id) {
        log.info("开始训练音色模型，ID: {}", id);
        customVoiceService.trainVoiceModel(id);
        return Result.success("训练任务已提交", null);
    }

    /**
     * 获取自定义音色列表（分页）
     */
    @GetMapping("/list")
    public Result<IPage<CustomVoice>> getCustomVoices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = securityUtils.getUserId();
        IPage<CustomVoice> voices = customVoiceService.getCustomVoicesByUserId(userId, page, size);
        return Result.success(voices);
    }

    /**
     * 获取音色广场分享的音色列表
     */
    @GetMapping("/square")
    public Result<List<CustomVoice>> getVoiceSquare() {
        return Result.success(customVoiceService.getPublicVoices());
    }

    /**
     * 分享或取消分享音色
     */
    @PostMapping("/share/{id}")
    public Result<Void> shareVoice(@PathVariable Long id, @RequestParam Integer isPublic) {
        Long userId = securityUtils.getUserId();
        customVoiceService.updateShareStatus(id, userId, isPublic);
        return Result.success();
    }

    /**
     * 删除自定义音色
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCustomVoice(@PathVariable Long id) {
        boolean success = customVoiceService.removeById(id);
        if (success) {
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }
}
