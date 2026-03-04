package com.was.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorApplicationDTO {
    @NotBlank(message = "申请理由不能为空")
    private String applicationReason;
}
