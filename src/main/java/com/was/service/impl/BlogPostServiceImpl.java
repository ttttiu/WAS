package com.was.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.was.mapper.BlogPostMapper;
import com.was.mapper.TagMapper;
import com.was.pojo.dto.BlogPostDTO;
import com.was.pojo.entity.BlogPost;
import com.was.pojo.entity.Tag;
import com.was.pojo.vo.BlogPostVO;
import com.was.pojo.vo.PageResult;
import com.was.pojo.vo.TagVO;
import com.was.service.BlogPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 博客文章服务实现类
 */
@Service
@Slf4j
public class BlogPostServiceImpl implements BlogPostService {

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    @Transactional
    public Long createPost(BlogPostDTO blogPostDTO, Long authorId) {
        BlogPost blogPost = new BlogPost();
        BeanUtils.copyProperties(blogPostDTO, blogPost);
        blogPost.setAuthorId(authorId);
        blogPost.setViewCount(0);
        blogPost.setLikeCount(0);
        blogPost.setCommentCount(0);
        
        // 如果是发布状态，设置发布时间
        if (blogPostDTO.getStatus() != null && blogPostDTO.getStatus() == 1) {
            blogPost.setPublishTime(LocalDateTime.now());
        }

        blogPostMapper.insert(blogPost);
        log.info("创建博客文章成功，ID: {}", blogPost.getId());

        // 保存标签关联
        if (blogPostDTO.getTagIds() != null && !blogPostDTO.getTagIds().isEmpty()) {
            tagMapper.insertPostTags(blogPost.getId(), blogPostDTO.getTagIds());
            log.info("保存文章标签关联成功，文章ID: {}, 标签数: {}", blogPost.getId(), blogPostDTO.getTagIds().size());
        }

        return blogPost.getId();
    }

    @Override
    @Transactional
    public void updatePost(Long id, BlogPostDTO blogPostDTO, Long authorId) {
        BlogPost existingPost = blogPostMapper.selectById(id);
        if (existingPost == null) {
            throw new RuntimeException("博客文章不存在");
        }

        // 验证是否是作者本人
        if (!existingPost.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权修改他人的博客文章");
        }

        BlogPost blogPost = new BlogPost();
        BeanUtils.copyProperties(blogPostDTO, blogPost);
        blogPost.setId(id);
        
        // 如果从草稿改为发布，设置发布时间
        if (existingPost.getStatus() == 0 && blogPostDTO.getStatus() != null && blogPostDTO.getStatus() == 1) {
            blogPost.setPublishTime(LocalDateTime.now());
        }

        blogPostMapper.updateById(blogPost);
        log.info("更新博客文章成功，ID: {}", id);

        // 更新标签关联
        if (blogPostDTO.getTagIds() != null) {
            tagMapper.deletePostTags(id);
            if (!blogPostDTO.getTagIds().isEmpty()) {
                tagMapper.insertPostTags(id, blogPostDTO.getTagIds());
            }
            log.info("更新文章标签关联成功，文章ID: {}", id);
        }
    }

    @Override
    @Transactional
    public void deletePost(Long id, Long authorId) {
        BlogPost existingPost = blogPostMapper.selectById(id);
        if (existingPost == null) {
            throw new RuntimeException("博客文章不存在");
        }

        // 验证是否是作者本人
        if (!existingPost.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权删除他人的博客文章");
        }

        // 删除标签关联
        tagMapper.deletePostTags(id);
        
        // 删除文章
        blogPostMapper.deleteById(id);
        log.info("删除博客文章成功，ID: {}", id);
    }

    @Override
    public BlogPostVO getPostById(Long id) {
        BlogPostVO postVO = blogPostMapper.selectPostDetailById(id);
        if (postVO == null) {
            throw new RuntimeException("博客文章不存在");
        }

        // 查询标签
        List<Tag> tags = tagMapper.selectTagsByPostId(id);
        List<TagVO> tagVOs = tags.stream()
                .map(tag -> TagVO.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .color(tag.getColor())
                        .createTime(tag.getCreateTime())
                        .build())
                .collect(Collectors.toList());
        postVO.setTags(tagVOs);

        return postVO;
    }

    @Override
    public PageResult<BlogPostVO> getPostPage(Integer pageNum, Integer pageSize, 
                                              String title, Long categoryId, 
                                              Integer status, Long authorId) {
        Page<BlogPostVO> page = new Page<>(pageNum, pageSize);
        IPage<BlogPostVO> resultPage = blogPostMapper.selectPostPage(page, title, categoryId, status, authorId);

        // 为每篇文章查询标签
        resultPage.getRecords().forEach(postVO -> {
            List<Tag> tags = tagMapper.selectTagsByPostId(postVO.getId());
            List<TagVO> tagVOs = tags.stream()
                    .map(tag -> TagVO.builder()
                            .id(tag.getId())
                            .name(tag.getName())
                            .color(tag.getColor())
                            .createTime(tag.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
            postVO.setTags(tagVOs);
        });

        return PageResult.of(resultPage.getTotal(), pageNum, pageSize, resultPage.getRecords());
    }

    @Override
    @Transactional
    public void publishPost(Long id, Long authorId) {
        BlogPost existingPost = blogPostMapper.selectById(id);
        if (existingPost == null) {
            throw new RuntimeException("博客文章不存在");
        }

        // 验证是否是作者本人
        if (!existingPost.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权发布他人的博客文章");
        }

        BlogPost blogPost = new BlogPost();
        blogPost.setId(id);
        blogPost.setStatus(1);
        blogPost.setPublishTime(LocalDateTime.now());
        blogPostMapper.updateById(blogPost);
        log.info("发布博客文章成功，ID: {}", id);
    }

    @Override
    public void incrementViewCount(Long id) {
        blogPostMapper.incrementViewCount(id);
    }
}
