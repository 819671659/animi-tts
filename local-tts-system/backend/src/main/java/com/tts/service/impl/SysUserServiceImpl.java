package com.tts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tts.dto.LoginDTO;
import com.tts.dto.RegisterDTO;
import com.tts.entity.SysUser;
import com.tts.mapper.SysUserMapper;
import com.tts.service.IEmailCodeService;
import com.tts.service.ISysUserService;
import com.tts.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 * 
 * @author TTS System
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private IEmailCodeService emailCodeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        // 1. 校验验证码
        if (!emailCodeService.verifyCode(registerDTO.getEmail(), registerDTO.getCode(), 1)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 2. 检查用户名是否已存在
        long count = this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, registerDTO.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 3. 检查邮箱是否已存在
        count = this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, registerDTO.getEmail()));
        if (count > 0) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 4. 保存用户
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        this.save(user);
    }

    @Override
    public String login(LoginDTO loginDTO) {
        // 使用 Spring Security 进行身份认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));

        // 认证成功，生成 Token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtils.generateToken(userDetails);
    }

    @Override
    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        // 1. 校验验证码
        if (!emailCodeService.verifyCode(email, code, 2)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 2. 获取用户
        SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, email));
        if (user == null) {
            throw new RuntimeException("该邮箱未注册");
        }

        // 3. 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }
}
