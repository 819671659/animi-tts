package com.tts.service.impl;

import com.tts.service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现类
 * 
 * @author TTS System
 */
@Service
public class EmailServiceImpl implements IEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendCodeEmail(String to, String code, Integer type) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);

        String subject = type == 1 ? "【本地 TTS 系统】注册验证码" : "【本地 TTS 系统】重置密码验证码";
        String content = "您的验证码为：" + code + "，有效期为 5 分钟。请勿泄露给他人。";

        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }
}
