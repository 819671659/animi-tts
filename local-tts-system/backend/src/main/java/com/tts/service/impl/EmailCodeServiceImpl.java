package com.tts.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tts.entity.EmailCodeVerify;
import com.tts.mapper.EmailCodeVerifyMapper;
import com.tts.service.IEmailCodeService;
import com.tts.service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 邮箱验证码服务实现类
 * 
 * @author TTS System
 */
@Service
public class EmailCodeServiceImpl extends ServiceImpl<EmailCodeVerifyMapper, EmailCodeVerify>
        implements IEmailCodeService {

    @Autowired
    private IEmailService emailService;

    @Override
    public void sendCode(String email, Integer type) {
        // 生成 6 位随机验证码
        String code = RandomUtil.randomNumbers(6);

        // 保存到数据库
        EmailCodeVerify verify = new EmailCodeVerify();
        verify.setEmail(email);
        verify.setCode(code);
        verify.setType(type);
        verify.setExpireTime(LocalDateTime.now().plusMinutes(5)); // 5 分钟有效期
        this.save(verify);

        // 发送邮件
        emailService.sendCodeEmail(email, code, type);
    }

    @Override
    public boolean verifyCode(String email, String code, Integer type) {
        EmailCodeVerify verify = this.getOne(new LambdaQueryWrapper<EmailCodeVerify>()
                .eq(EmailCodeVerify::getEmail, email)
                .eq(EmailCodeVerify::getCode, code)
                .eq(EmailCodeVerify::getType, type)
                .gt(EmailCodeVerify::getExpireTime, LocalDateTime.now())
                .orderByDesc(EmailCodeVerify::getCreateTime)
                .last("limit 1"));

        return verify != null;
    }
}
