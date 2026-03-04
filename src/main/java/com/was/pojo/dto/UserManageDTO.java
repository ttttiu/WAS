package com.was.pojo.dto;

import lombok.Data;

@Data
public class UserManageDTO {
    private Long userId;
    private Long roleId;
    private Integer accountStatus; // 0-正常, 1-禁用
    private String disableReason;
}
