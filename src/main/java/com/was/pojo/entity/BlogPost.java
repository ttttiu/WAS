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

/**
 * 博客文章实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("blog_post")
public class BlogPost implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String summary;

    private String coverImage;

    private Long authorId;

    private Long categoryId;

    private Integer status;

    private Integer isTop;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime publishTime;
}
