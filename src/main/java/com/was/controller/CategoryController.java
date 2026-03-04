package com.was.controller;

import com.was.annotation.RequirePermission;
import com.was.pojo.Result;
import com.was.pojo.entity.Category;
import com.was.pojo.vo.CategoryVO;
import com.was.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 创建分类
     */
    @PostMapping
    @RequirePermission("category:create")
    public Result<Long> createCategory(@RequestBody Category category) {
        Long categoryId = categoryService.createCategory(category);
        log.info("创建分类成功，ID: {}", categoryId);
        return Result.success(categoryId);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @RequirePermission("category:edit")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateCategory(category);
        log.info("更新分类成功，ID: {}", id);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @RequirePermission("category:delete")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        log.info("删除分类成功，ID: {}", id);
        return Result.success();
    }

    /**
     * 查询分类详情
     */
    @GetMapping("/{id}")
    public Result<CategoryVO> getCategory(@PathVariable Long id) {
        CategoryVO categoryVO = categoryService.getCategoryById(id);
        return Result.success(categoryVO);
    }

    /**
     * 查询所有分类
     */
    @GetMapping("/list")
    public Result<List<CategoryVO>> getAllCategories() {
        List<CategoryVO> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }
}
