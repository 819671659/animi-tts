package com.tts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tts.entity.EmailCodeVerify;

/**
 * 邮箱验证码服务接口
 * 
 * @author TTS System
 */
public interface IEmailCodeService extends IService<EmailCodeVerify> {

    /**
     * 生成并发送验证码
     * 
     * @param email 邮箱
     * @param type  类型
     */
    void sendCode(String email, Integer type);

    /**
     * 校验验证码
     * 
     * @param email 邮箱
     * @param code  验证码
     * @param type  类型
     * @return 是否通过
     */
    boolean verifyCode(String email, String code, Integer type);
}
