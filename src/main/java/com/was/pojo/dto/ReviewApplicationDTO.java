package com.was.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewApplicationDTO {
    @NotNull(message = "申请ID不能为空")
    private Long applicationId;
    
    @NotNull(message = "审核状态不能为空")
    private Integer status; // 1-批准, 2-拒绝
    
    private String reviewReason;
}
