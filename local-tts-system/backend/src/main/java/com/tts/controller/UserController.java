package com.tts.controller;

import com.tts.common.Result;
import com.tts.entity.SysUser;
import com.tts.service.ISysUserService;
import com.tts.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 
 * @author TTS System
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<SysUser> getInfo() {
        SysUser user = securityUtils.getCurrentUser();
        if (user != null) {
            user.setPassword(null); // 隐藏密码
        }
        return Result.success(user);
    }

    /**
     * 修改密码
     */
    @PostMapping("/password/update")
    public Result<Void> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        Long userId = securityUtils.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }
}
