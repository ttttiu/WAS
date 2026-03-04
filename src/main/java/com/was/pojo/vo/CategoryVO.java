package com.was.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 分类VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVO {

    private Long id;

    private String name;

    private String description;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
