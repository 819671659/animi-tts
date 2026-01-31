package com.tts.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tts.entity.CustomVoice;
import com.tts.mapper.CustomVoiceMapper;
import com.tts.service.ICustomVoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * 自定义音色服务实现类
 * 
 * @author TTS System
 */
@Slf4j
@Service
public class CustomVoiceServiceImpl extends ServiceImpl<CustomVoiceMapper, CustomVoice> implements ICustomVoiceService {

    @Value("${tts.audio-base-path}")
    private String audioBasePath;

    @Override
    public CustomVoice uploadVoiceSample(Long userId, String voiceName, MultipartFile audioFile, String description,
            String sampleText) {
        try {
            // 生成唯一文件名
            String originalFilename = audioFile.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new RuntimeException("非法的文件名");
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "sample_" + IdUtil.simpleUUID() + extension;
            String subDir = "samples/";
            String fullDirPath = audioBasePath + subDir;

            // 确保目录存在
            FileUtil.mkdir(fullDirPath);

            // 保存文件
            File dest = new File(fullDirPath + fileName);
            audioFile.transferTo(dest);

            // 获取音频时长（可选，后续实现）
            // TODO: 使用 ffmpeg 或其他工具获取音频时长

            // 创建自定义音色记录
            CustomVoice customVoice = new CustomVoice();
            customVoice.setUserId(userId);
            customVoice.setIsPublic(0); // 默认私有
            customVoice.setVoiceName(voiceName);
            customVoice.setSampleAudioUrl("/api/audios/samples/" + fileName);
            customVoice.setDescription(description);
            customVoice.setSampleText(sampleText);
            customVoice.setTrainingStatus(0); // 待训练

            this.save(customVoice);

            log.info("音色样本上传成功: {}, ID: {}", voiceName, customVoice.getId());
            return customVoice;

        } catch (Exception e) {
            log.error("上传音色样本失败", e);
            throw new RuntimeException("上传音色样本失败: " + e.getMessage());
        }
    }

    @Override
    public void trainVoiceModel(Long id) {
        CustomVoice customVoice = this.getById(id);
        if (customVoice == null) {
            throw new RuntimeException("音色不存在");
        }

        if (customVoice.getTrainingStatus() == 1) {
            throw new RuntimeException("音色正在训练中，请勿重复提交");
        }

        if (customVoice.getTrainingStatus() == 2) {
            throw new RuntimeException("音色已经训练完成");
        }

        // 更新状态为训练中
        customVoice.setTrainingStatus(1);
        this.updateById(customVoice);

        // 模拟训练/分析过程
        // 实际上 Zero-shot 不需要微调，但我们可以检查文件有效性
        try {
            String sampleUrl = customVoice.getSampleAudioUrl();
            String cleanUrl = sampleUrl;
            if (cleanUrl.startsWith("/api/audios/")) {
                cleanUrl = cleanUrl.replace("/api/audios/", "");
            }

            File sampleFile = new File(audioBasePath + cleanUrl);
            if (!sampleFile.exists()) {
                throw new RuntimeException("音频样本文件丢失");
            }

            // TODO: 这里可以调用 python 脚本做进一步的特征检查
            // 目前简单认为文件存在即“训练”成功 (Zero-shot ready)

            Thread.sleep(1000); // 模拟耗时

            customVoice.setTrainingStatus(2); // 训练完成
            customVoice.setModelPath(sampleFile.getAbsolutePath()); // 记录模型路径(即音频本身)
            this.updateById(customVoice);

            log.info("音色分析完成: {}", id);

        } catch (Exception e) {
            log.error("音色分析失败", e);
            customVoice.setTrainingStatus(3); // 失败
            this.updateById(customVoice);
            throw new RuntimeException("音色分析失败: " + e.getMessage());
        }
    }

    @Override
    public IPage<CustomVoice> getCustomVoicesByUserId(Long userId, Integer page, Integer size) {
        Page<CustomVoice> pageParam = new Page<>(page, size);
        return this.page(pageParam, new LambdaQueryWrapper<CustomVoice>()
                .eq(CustomVoice::getUserId, userId));
    }

    @Override
    public List<CustomVoice> getPublicVoices() {
        return this.list(new LambdaQueryWrapper<CustomVoice>()
                .eq(CustomVoice::getIsPublic, 1));
    }

    @Override
    public void updateShareStatus(Long id, Long userId, Integer isPublic) {
        CustomVoice voice = this.getOne(new LambdaQueryWrapper<CustomVoice>()
                .eq(CustomVoice::getId, id)
                .eq(CustomVoice::getUserId, userId));
        if (voice == null) {
            throw new RuntimeException("音色不存在或无权操作");
        }
        voice.setIsPublic(isPublic);
        this.updateById(voice);
    }

    @Override
    public boolean removeById(java.io.Serializable id) {
        CustomVoice voice = this.getById(id);
        if (voice != null) {
            // 1. 删除物理文件
            String sampleUrl = voice.getSampleAudioUrl();
            if (sampleUrl != null && sampleUrl.startsWith("/api/audios/")) {
                String relativePath = sampleUrl.substring("/api/audios/".length());
                File file = new File(audioBasePath + relativePath);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    log.info("删除音色样本物理文件: {}, 结果: {}", file.getAbsolutePath(), deleted);
                }
            }
            // 2. 物理删除数据库记录 (MyBatis-Plus 在没有 @TableLogic 时会执行 DELETE)
            return super.removeById(id);
        }
        return false;
    }
}
