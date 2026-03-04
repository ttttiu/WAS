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
@TableName("author_application")
public class AuthorApplication implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String applicationReason;
    
    private Integer status; // 0-待审核, 1-已批准, 2-已拒绝
    
    private String reviewReason;
    
    private Long reviewerId;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
