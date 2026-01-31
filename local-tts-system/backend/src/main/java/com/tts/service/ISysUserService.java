package com.tts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tts.dto.LoginDTO;
import com.tts.dto.RegisterDTO;
import com.tts.entity.SysUser;

/**
 * 用户服务接口
 * 
 * @author TTS System
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 用户注册
     * 
     * @param registerDTO 注册信息
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     * 
     * @param loginDTO 登录信息
     * @return JWT Token
     */
    String login(LoginDTO loginDTO);

    /**
     * 重置密码
     * 
     * @param email       邮箱
     * @param code        验证码
     * @param newPassword 新密码
     */
    void resetPassword(String email, String code, String newPassword);

    /**
     * 修改密码
     * 
     * @param userId      用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);
}
