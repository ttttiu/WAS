package com.was.controller;

import com.was.annotation.RequirePermission;
import com.was.pojo.Result;
import com.was.pojo.dto.BlogPostDTO;
import com.was.pojo.entity.LoginUser;
import com.was.pojo.vo.BlogPostVO;
import com.was.pojo.vo.PageResult;
import com.was.service.BlogPostService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 博客文章控制器
 */
@RestController
@RequestMapping("/blog")
@Slf4j
public class BlogPostController {

    @Autowired
    private BlogPostService blogPostService;

    /**
     * 创建博客文章
     */
    @PostMapping
    @RequirePermission("blog:create")
    public Result<Long> createPost(@Valid @RequestBody BlogPostDTO blogPostDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return Result.error(errorMessage);
        }

        Long authorId = getCurrentUserId();
        Long postId = blogPostService.createPost(blogPostDTO, authorId);
        log.info("用户 {} 创建博客文章成功，ID: {}", authorId, postId);
        return Result.success(postId);
    }

    /**
     * 更新博客文章
     */
    @PutMapping("/{id}")
    @RequirePermission("blog:edit")
    public Result<Void> updatePost(@PathVariable Long id, 
                                   @Valid @RequestBody BlogPostDTO blogPostDTO,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return Result.error(errorMessage);
        }

        Long authorId = getCurrentUserId();
        blogPostService.updatePost(id, blogPostDTO, authorId);
        log.info("用户 {} 更新博客文章成功，ID: {}", authorId, id);
        return Result.success();
    }

    /**
     * 删除博客文章
     */
    @DeleteMapping("/{id}")
    @RequirePermission("blog:delete")
    public Result<Void> deletePost(@PathVariable Long id) {
        Long authorId = getCurrentUserId();
        blogPostService.deletePost(id, authorId);
        log.info("用户 {} 删除博客文章成功，ID: {}", authorId, id);
        return Result.success();
    }

    /**
     * 查询博客文章详情
     */
    @GetMapping("/{id}")
    public Result<BlogPostVO> getPost(@PathVariable Long id) {
        BlogPostVO postVO = blogPostService.getPostById(id);
        // 增加浏览次数
        blogPostService.incrementViewCount(id);
        return Result.success(postVO);
    }

    /**
     * 分页查询博客列表
     */
    @GetMapping("/page")
    public Result<PageResult<BlogPostVO>> getPostPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long authorId) {
        
        PageResult<BlogPostVO> pageResult = blogPostService.getPostPage(
                pageNum, pageSize, title, categoryId, status, authorId);
        return Result.success(pageResult);
    }

    /**
     * 发布博客文章
     */
    @PostMapping("/{id}/publish")
    @RequirePermission("blog:publish")
    public Result<Void> publishPost(@PathVariable Long id) {
        Long authorId = getCurrentUserId();
        blogPostService.publishPost(id, authorId);
        log.info("用户 {} 发布博客文章成功，ID: {}", authorId, id);
        return Result.success();
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser().getId();
    }
}
