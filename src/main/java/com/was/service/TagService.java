package com.was.service;

import com.was.pojo.entity.Tag;
import com.was.pojo.vo.TagVO;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {

    /**
     * 创建标签
     */
    Long createTag(Tag tag);

    /**
     * 更新标签
     */
    void updateTag(Tag tag);

    /**
     * 删除标签
     */
    void deleteTag(Long id);

    /**
     * 根据ID查询标签
     */
    TagVO getTagById(Long id);

    /**
     * 查询所有标签
     */
    List<TagVO> getAllTags();
}
