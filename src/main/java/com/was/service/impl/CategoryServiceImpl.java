package com.was.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.was.mapper.CategoryMapper;
import com.was.pojo.entity.Category;
import com.was.pojo.vo.CategoryVO;
import com.was.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Long createCategory(Category category) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName());
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("分类名称已存在");
        }

        categoryMapper.insert(category);
        log.info("创建分类成功，ID: {}", category.getId());
        return category.getId();
    }

    @Override
    public void updateCategory(Category category) {
        Category existingCategory = categoryMapper.selectById(category.getId());
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查分类名称是否与其他分类重复
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName())
               .ne(Category::getId, category.getId());
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("分类名称已存在");
        }

        categoryMapper.updateById(category);
        log.info("更新分类成功，ID: {}", category.getId());
    }

    @Override
    public void deleteCategory(Long id) {
        Category existingCategory = categoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }

        categoryMapper.deleteById(id);
        log.info("删除分类成功，ID: {}", id);
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);
        return categoryVO;
    }

    @Override
    public List<CategoryVO> getAllCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 1)
               .orderByAsc(Category::getSortOrder);
        
        List<Category> categories = categoryMapper.selectList(wrapper);
        return categories.stream()
                .map(category -> {
                    CategoryVO categoryVO = new CategoryVO();
                    BeanUtils.copyProperties(category, categoryVO);
                    return categoryVO;
                })
                .collect(Collectors.toList());
    }
}
