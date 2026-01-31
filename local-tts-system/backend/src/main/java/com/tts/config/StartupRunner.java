package com.tts.config;

import com.tts.utils.EnvironmentChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动后执行的初始化任务
 * 
 * @author TTS System
 */
@Slf4j
@Component
public class StartupRunner implements ApplicationRunner {

    @Autowired
    private EnvironmentChecker environmentChecker;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("");
        log.info("====================================");
        log.info("执行启动检查...");
        log.info("====================================");

        // 检查环境依赖
        boolean dependenciesOk = environmentChecker.checkAllDependencies();

        if (!dependenciesOk) {
            log.warn("");
            log.warn("⚠️  警告：环境依赖检测未通过！");
            log.warn("⚠️  部分功能可能无法正常使用，请按照上述指南修复");
            log.warn("");
        }

        log.info("");
        log.info("====================================");
        log.info("应用启动完成！");
        log.info("访问地址: http://localhost:2211");
        log.info("====================================");
        log.info("");
    }
}
