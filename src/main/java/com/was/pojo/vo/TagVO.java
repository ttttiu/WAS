package com.was.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagVO {

    private Long id;

    private String name;

    private String color;

    private LocalDateTime createTime;
}
