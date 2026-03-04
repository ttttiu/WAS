package com.was.pojo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userName;

    private String password;

    private String email;
    
    private String avatar;
    
    private String nickname;
    
    private Integer status;
    
    private Integer accountStatus; // 账户状态: 0-正常, 1-禁用
    
    private String disableReason; // 禁用原因
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
