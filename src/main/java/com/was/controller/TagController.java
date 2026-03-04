package com.was.controller;

import com.was.annotation.RequirePermission;
import com.was.pojo.Result;
import com.was.pojo.entity.Tag;
import com.was.pojo.vo.TagVO;
import com.was.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/tag")
@Slf4j
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 创建标签
     */
    @PostMapping
    @RequirePermission("tag:create")
    public Result<Long> createTag(@RequestBody Tag tag) {
        Long tagId = tagService.createTag(tag);
        log.info("创建标签成功，ID: {}", tagId);
        return Result.success(tagId);
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    @RequirePermission("tag:edit")
    public Result<Void> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        tag.setId(id);
        tagService.updateTag(tag);
        log.info("更新标签成功，ID: {}", id);
        return Result.success();
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    @RequirePermission("tag:delete")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        log.info("删除标签成功，ID: {}", id);
        return Result.success();
    }

    /**
     * 查询标签详情
     */
    @GetMapping("/{id}")
    public Result<TagVO> getTag(@PathVariable Long id) {
        TagVO tagVO = tagService.getTagById(id);
        return Result.success(tagVO);
    }

    /**
     * 查询所有标签
     */
    @GetMapping("/list")
    public Result<List<TagVO>> getAllTags() {
        List<TagVO> tags = tagService.getAllTags();
        return Result.success(tags);
    }
}
