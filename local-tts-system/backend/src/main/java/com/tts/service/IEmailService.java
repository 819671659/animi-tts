package com.tts.service;

/**
 * 邮件服务接口
 * 
 * @author TTS System
 */
public interface IEmailService {

    /**
     * 发送验证码邮件
     * 
     * @param to   接收人邮件
     * @param code 验证码
     * @param type 类型（1-注册，2-找回密码）
     */
    void sendCodeEmail(String to, String code, Integer type);
}
