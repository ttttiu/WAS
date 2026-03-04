package com.was.service;

import com.was.pojo.dto.BlogPostDTO;
import com.was.pojo.vo.BlogPostVO;
import com.was.pojo.vo.PageResult;

/**
 * 博客文章服务接口
 */
public interface BlogPostService {

    /**
     * 创建博客文章
     */
    Long createPost(BlogPostDTO blogPostDTO, Long authorId);

    /**
     * 更新博客文章
     */
    void updatePost(Long id, BlogPostDTO blogPostDTO, Long authorId);

    /**
     * 删除博客文章
     */
    void deletePost(Long id, Long authorId);

    /**
     * 根据ID查询博客详情
     */
    BlogPostVO getPostById(Long id);

    /**
     * 分页查询博客列表
     */
    PageResult<BlogPostVO> getPostPage(Integer pageNum, Integer pageSize, 
                                       String title, Long categoryId, 
                                       Integer status, Long authorId);

    /**
     * 发布博客
     */
    void publishPost(Long id, Long authorId);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long id);
}
