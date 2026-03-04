package com.was.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 博客文章DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogPostDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String summary;

    private String coverImage;

    private Long categoryId;

    private List<Long> tagIds;

    private Integer status; // 0-草稿 1-发布

    private Integer isTop; // 0-否 1-是
}
