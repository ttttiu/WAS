package com.was.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客文章VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogPostVO {

    private Long id;

    private String title;

    private String content;

    private String summary;

    private String coverImage;

    private Long authorId;

    private String authorName;

    private Long categoryId;

    private String categoryName;

    private List<TagVO> tags;

    private Integer status;

    private Integer isTop;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime publishTime;
}
