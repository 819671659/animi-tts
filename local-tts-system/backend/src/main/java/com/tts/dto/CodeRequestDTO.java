package com.tts.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 验证码请求 DTO
 */
@Data
public class CodeRequestDTO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 类型：REGISTER 或 RESET_PASSWORD
     */
    @NotBlank(message = "类型不能为空")
    private String type;
}
