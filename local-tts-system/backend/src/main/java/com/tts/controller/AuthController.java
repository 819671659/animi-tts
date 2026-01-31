package com.tts.controller;

import com.tts.common.Result;
import com.tts.dto.LoginDTO;
import com.tts.dto.RegisterDTO;
import com.tts.dto.CodeRequestDTO;
import com.tts.dto.ForgetPasswordDTO;
import com.tts.service.IEmailCodeService;
import com.tts.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IEmailCodeService emailCodeService;

    /**
     * 发送验证码 (POST 适配前端)
     */
    @PostMapping("/code/send")
    public Result<Void> sendCode(@Validated @RequestBody CodeRequestDTO request) {
        Integer typeNum = "REGISTER".equals(request.getType()) ? 1 : 2;
        emailCodeService.sendCode(request.getEmail(), typeNum);
        return Result.success();
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Validated @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return Result.success(map);
    }

    /**
     * 重置密码 (接口路径适配前端)
     */
    @PostMapping("/password/reset")
    public Result<Void> resetPassword(@Validated @RequestBody ForgetPasswordDTO request) {
        userService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return Result.success();
    }
}
