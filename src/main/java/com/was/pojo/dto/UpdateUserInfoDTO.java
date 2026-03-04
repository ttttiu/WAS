package com.was.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户信息DTO
 */
@Data
public class UpdateUserInfoDTO implements Serializable {

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;
}
