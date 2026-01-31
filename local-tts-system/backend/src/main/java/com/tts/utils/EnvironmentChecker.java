package com.tts.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 环境依赖检测工具
 * 用于检测 Python 和 edge-tts 是否正确安装
 * 
 * @author TTS System
 */
@Slf4j
@Component
public class EnvironmentChecker {

    @Value("${tts.python-path}")
    private String pythonPath;

    /**
     * 检查所有依赖环境
     * 
     * @return true-所有依赖正常，false-存在缺失
     */
    public boolean checkAllDependencies() {
        log.info("====================================");
        log.info("开始检测环境依赖...");
        log.info("====================================");

        boolean pythonOk = checkPython();
        boolean edgeTtsOk = checkEdgeTts();

        log.info("====================================");
        if (pythonOk && edgeTtsOk) {
            log.info("✅ 环境依赖检测通过！");
        } else {
            log.error("❌ 环境依赖检测失败，请按照提示修复问题");
        }
        log.info("====================================");

        return pythonOk && edgeTtsOk;
    }

    /**
     * 检查 Python 是否可用
     */
    private boolean checkPython() {
        try {
            ProcessBuilder pb = new ProcessBuilder(pythonPath, "--version");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String version = reader.readLine();

            int exitCode = process.waitFor();

            if (exitCode == 0 && version != null) {
                log.info("✅ Python 检测成功: {}", version);
                return true;
            } else {
                log.error("❌ Python 检测失败！");
                log.error("   配置路径: {}", pythonPath);
                log.error("   退出码: {}", exitCode);
                printPythonInstallGuide();
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Python 检测异常: {}", e.getMessage());
            log.error("   配置路径: {}", pythonPath);
            printPythonInstallGuide();
            return false;
        }
    }

    /**
     * 检查 edge-tts 是否已安装
     */
    private boolean checkEdgeTts() {
        try {
            ProcessBuilder pb = new ProcessBuilder(pythonPath, "-m", "edge_tts", "--version");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("✅ edge-tts 检测成功: {}", output.toString());
                return true;
            } else {
                log.error("❌ edge-tts 检测失败！");
                log.error("   错误信息: {}", output.toString());
                printEdgeTtsInstallGuide();
                return false;
            }
        } catch (Exception e) {
            log.error("❌ edge-tts 检测异常: {}", e.getMessage());
            printEdgeTtsInstallGuide();
            return false;
        }
    }

    /**
     * 打印 Python 安装指南
     */
    private void printPythonInstallGuide() {
        log.error("");
        log.error("📖 Python 安装指南：");
        log.error("   1. 下载 Python: https://www.python.org/downloads/");
        log.error("   2. 安装时勾选 'Add Python to PATH'");
        log.error("   3. 验证安装: python --version");
        log.error("   4. 如果已安装但路径不对，请修改 application.yml 中的 tts.python-path");
        log.error("");
    }

    /**
     * 打印 edge-tts 安装指南
     */
    private void printEdgeTtsInstallGuide() {
        log.error("");
        log.error("📖 edge-tts 安装指南：");
        log.error("   1. 打开命令行 (CMD)");
        log.error("   2. 执行命令: pip install edge-tts");
        log.error("   3. 验证安装: python -m edge_tts --version");
        log.error("   4. 重启本应用");
        log.error("");
    }
}
