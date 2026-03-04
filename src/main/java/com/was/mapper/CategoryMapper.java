package com.was.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.was.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
