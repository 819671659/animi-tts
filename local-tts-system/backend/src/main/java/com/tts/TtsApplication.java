package com.tts;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 本地文字配音系统启动类
 * 
 * @author TTS System
 * @since 2026-01-27
 */
@SpringBootApplication
@MapperScan("com.tts.mapper")
public class TtsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtsApplication.class, args);
    }
}
