package com.tts.utils;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * TTS 引擎调用工具类
 * 通过 ProcessBuilder 调用 Python 脚本执行 TTS
 * 
 * @author TTS System
 */
@Slf4j
@Component
public class TtsEngineUtil {

    @Value("${tts.python-path}")
    private String pythonPath;

    @Value("${tts.script-path}")
    private String scriptPath;

    @Value("${tts.audio-base-path}")
    private String audioBasePath;

    /**
     * 生成语音文件（使用 edge-tts）
     *
     * @param text  文本内容
     * @param voice 语音角色
     * @param rate  语速
     * @param pitch 音调
     * @return 音频文件名
     */
    public String generateWithEdgeTts(String text, String voice, String rate, String pitch, String subDir) {
        try {
            // 生成唯一文件名
            String fileName = "tts_" + System.currentTimeMillis() + ".mp3";

            // 使用绝对路径 + 子目录
            String finalDir = audioBasePath + (subDir.endsWith("/") ? subDir : subDir + "/");
            File audioDir = new File(finalDir);
            if (!audioDir.exists()) {
                audioDir.mkdirs();
                log.info("创建音频输出目录: {}", audioDir.getAbsolutePath());
            }

            File outputFile = new File(audioDir, fileName);
            String outputPath = outputFile.getAbsolutePath();

            // 脚本绝对路径
            File scriptFile = new File(scriptPath + "edge_tts_generator.py");
            String scriptAbsPath = scriptFile.getAbsolutePath();

            log.info("========== TTS 生成参数 ==========");
            log.info("Python 路径: {}", pythonPath);
            log.info("脚本路径: {}", scriptAbsPath);
            log.info("输出路径: {}", outputPath);
            log.info("文本长度: {}", text.length());
            log.info("语音角色: {}", voice);
            log.info("================================");

            // 检查脚本文件是否存在
            if (!scriptFile.exists()) {
                throw new RuntimeException("Python 脚本不存在: " + scriptAbsPath);
            }

            // 构建 Python 命令
            ProcessBuilder pb = new ProcessBuilder(
                    pythonPath,
                    scriptAbsPath,
                    "--text", text,
                    "--voice", voice,
                    "--rate", rate,
                    "--pitch", pitch,
                    "--output", outputPath);

            // 重定向错误输出
            pb.redirectErrorStream(true);

            log.info("开始执行 Python 命令...");

            Process process = pb.start();

            // 读取进程输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("Python >> {}", line);
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            log.info("Python 进程退出码: {}", exitCode);

            if (exitCode != 0) {
                log.error("Python 输出:\n{}", output.toString());
                throw new RuntimeException("TTS 生成失败，退出码: " + exitCode);
            }

            // 检查文件是否真的生成了
            if (!outputFile.exists()) {
                throw new RuntimeException("音频文件未生成: " + outputPath);
            }

            log.info("✅ 语音生成成功: {} ({}字节)", fileName, outputFile.length());
            return fileName;

        } catch (Exception e) {
            log.error("❌ TTS 引擎调用失败", e);
            throw new RuntimeException("语音生成失败: " + e.getMessage());
        }
    }

    /**
     * 使用自定义音色生成语音（调用 clone_voice.py）
     *
     * @param text      文本内容
     * @param sampleUrl 自定义音色模型路径/参考音频URL
     * @return 音频文件名
     */
    public String generateWithCustomVoice(String text, String sampleUrl) {
        try {
            // 生成唯一文件名
            String fileName = "clone_" + System.currentTimeMillis() + ".wav";

            // 使用 absolute path for output
            // sampleUrl 可能是相对路径 "samples/xxx.mp3" 或完整路径
            // 我们假设它在 audioBasePath 下，或者是一个完整的 HTTP URL（这里暂定为本地文件路径）

            // 1. Resolve Reference Audio Path
            // 数据库存的可能是 "/api/audios/samples/xxx.wav"
            String cleanSampleUrl = sampleUrl;
            if (cleanSampleUrl != null && cleanSampleUrl.startsWith("/api/audios/")) {
                cleanSampleUrl = cleanSampleUrl.substring("/api/audios/".length());
            }

            File refFile;
            if (FileUtil.isAbsolutePath(cleanSampleUrl)) {
                refFile = new File(cleanSampleUrl);
            } else {
                // 如果是相对路径，假设它在 audioBasePath 下
                refFile = new File(audioBasePath + cleanSampleUrl);
            }

            if (!refFile.exists()) {
                log.error("参考音频文件不存在: {}", refFile.getAbsolutePath());
                // Fallback: 如果参考音频没了，只能报错或者用默认音色
                throw new RuntimeException("参考音频文件丢失，无法进行克隆生成");
            }

            // 2. Prepare Output Path
            String subDir = "records/";
            String finalDir = audioBasePath + (subDir.endsWith("/") ? subDir : subDir + "/");
            File audioDir = new File(finalDir);
            if (!audioDir.exists()) {
                audioDir.mkdirs();
            }
            File outputFile = new File(audioDir, fileName);
            String outputAbsPath = outputFile.getAbsolutePath();

            // 3. Prepare Python Script
            File scriptFile = new File(scriptPath + "clone_voice.py");
            if (!scriptFile.exists()) {
                throw new RuntimeException("克隆脚本不存在: " + scriptFile.getAbsolutePath());
            }

            log.info("========== 开始执行音色克隆 ==========");
            log.info("参考音频: {}", refFile.getAbsolutePath());
            log.info("文本内容: {}", text);
            log.info("输出路径: {}", outputAbsPath);

            // 4. Build Process
            ProcessBuilder pb = new ProcessBuilder(
                    pythonPath,
                    scriptFile.getAbsolutePath(),
                    "--text", text,
                    "--ref_audio", refFile.getAbsolutePath(),
                    "--output", outputAbsPath);

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read Output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("CloneScript >> {}", line);
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("克隆脚本执行失败，退出码: {}", exitCode);
                log.error("脚本日志:\n{}", output.toString());
                throw new RuntimeException("音色克隆失败，可能是缺少 Python 依赖 (TTS)。请检查服务器日志。");
            }

            if (!outputFile.exists()) {
                throw new RuntimeException("生成文件丢失");
            }

            log.info("✅ 音色克隆成功: {}", fileName);
            return fileName;

        } catch (Exception e) {
            log.error("自定义音色 TTS 调用失败", e);
            throw new RuntimeException("自定义音色生成失败: " + e.getMessage());
        }
    }
}
