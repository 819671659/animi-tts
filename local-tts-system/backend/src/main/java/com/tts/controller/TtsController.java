package com.tts.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tts.common.Result;
import com.tts.dto.TtsRequestDTO;
import com.tts.dto.TtsResponseDTO;
import com.tts.entity.TtsRecord;
import com.tts.entity.VoiceConfig;
import com.tts.service.ITtsService;
import com.tts.service.IVoiceConfigService;
import com.tts.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * TTS 控制器
 * 
 * @author TTS System
 */
@Slf4j
@RestController
@RequestMapping("/api/tts")
public class TtsController {

    @Autowired
    private ITtsService ttsService;

    @Autowired
    private IVoiceConfigService voiceConfigService;

    @Autowired
    private SecurityUtils securityUtils;

    @Value("${tts.audio-base-path}")
    private String audioBasePath;

    /**
     * 生成语音
     */
    @PostMapping("/generate")
    public Result<TtsResponseDTO> generateTts(@RequestBody TtsRequestDTO request) {
        Long userId = securityUtils.getUserId();
        log.info("收到用户 {} 的 TTS 生成请求，文本长度: {}", userId, request.getText().length());
        TtsResponseDTO response = ttsService.generateTts(userId, request);
        return Result.success(response);
    }

    /**
     * 获取可用语音列表（预设）
     */
    @GetMapping("/voices")
    public Result<List<VoiceConfig>> getVoiceList() {
        List<VoiceConfig> voices = voiceConfigService.getAllVoices();
        return Result.success(voices);
    }

    /**
     * 下载音频文件
     */
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadAudio(@PathVariable String filename) {
        try {
            // 注意：这里为了兼容性，如果 filename 包含 records/ 前缀则直接使用，否则假设在 records/ 下
            String path = filename.contains("/") ? filename : "records/" + filename;
            File file = new File(audioBasePath + path);
            if (!file.exists()) {
                throw new RuntimeException("文件不存在");
            }

            Resource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("下载文件失败: {}", filename, e);
            throw new RuntimeException("下载文件失败: " + e.getMessage());
        }
    }

    /**
     * 查询历史记录（分页）
     */
    @GetMapping("/history")
    public Result<IPage<TtsRecord>> getHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = securityUtils.getUserId();
        IPage<TtsRecord> history = ttsService.getHistoryByUserId(userId, page, size);
        return Result.success(history);
    }

    /**
     * 重命名历史记录
     */
    @PostMapping("/rename/{id}")
    public Result<Void> renameRecord(@PathVariable Long id, @RequestParam String newName) {
        Long userId = securityUtils.getUserId();
        ttsService.renameRecord(id, userId, newName);
        return Result.success();
    }

    /**
     * 删除历史记录
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        Long userId = securityUtils.getUserId();
        ttsService.deleteRecord(id, userId);
        return Result.success("删除成功", null);
    }
}
