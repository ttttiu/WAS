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
public class AuthorApplicationVO {
    private Long id;
    private Long userId;
    private String userName;
    private String nickname;
    private String applicationReason;
    private Integer status;
    private String reviewReason;
    private Long reviewerId;
    private String reviewerName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
