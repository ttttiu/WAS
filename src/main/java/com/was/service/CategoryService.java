package com.was.service;

import com.was.pojo.entity.Category;
import com.was.pojo.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 创建分类
     */
    Long createCategory(Category category);

    /**
     * 更新分类
     */
    void updateCategory(Category category);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);

    /**
     * 根据ID查询分类
     */
    CategoryVO getCategoryById(Long id);

    /**
     * 查询所有分类
     */
    List<CategoryVO> getAllCategories();
}
