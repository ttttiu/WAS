package com.was.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserManageVO {
    private Long id;
    private String userName;
    private String email;
    private String nickname;
    private String avatar;
    private Integer accountStatus;
    private String disableReason;
    private Long roleId;
    private String roleName;
    private LocalDateTime createTime;
}
