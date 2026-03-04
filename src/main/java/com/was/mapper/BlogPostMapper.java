package com.was.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.was.pojo.entity.BlogPost;
import com.was.pojo.vo.BlogPostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 博客文章Mapper
 */
@Mapper
public interface BlogPostMapper extends BaseMapper<BlogPost> {

    /**
     * 分页查询博客文章（带作者和分类信息）
     */
    IPage<BlogPostVO> selectPostPage(Page<BlogPostVO> page, 
                                     @Param("title") String title,
                                     @Param("categoryId") Long categoryId,
                                     @Param("status") Integer status,
                                     @Param("authorId") Long authorId);

    /**
     * 根据ID查询博客详情（带作者和分类信息）
     */
    BlogPostVO selectPostDetailById(@Param("id") Long id);

    /**
     * 增加浏览次数
     */
    @Update("UPDATE blog_post SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 增加点赞次数
     */
    @Update("UPDATE blog_post SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞次数
     */
    @Update("UPDATE blog_post SET like_count = like_count - 1 WHERE id = #{id} AND like_count > 0")
    void decrementLikeCount(@Param("id") Long id);
}
