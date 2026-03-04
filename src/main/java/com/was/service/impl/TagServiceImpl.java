package com.was.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.was.mapper.TagMapper;
import com.was.pojo.entity.Tag;
import com.was.pojo.vo.TagVO;
import com.was.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 */
@Service
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public Long createTag(Tag tag) {
        // 检查标签名称是否已存在
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tag.getName());
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("标签名称已存在");
        }

        tagMapper.insert(tag);
        log.info("创建标签成功，ID: {}", tag.getId());
        return tag.getId();
    }

    @Override
    public void updateTag(Tag tag) {
        Tag existingTag = tagMapper.selectById(tag.getId());
        if (existingTag == null) {
            throw new RuntimeException("标签不存在");
        }

        // 检查标签名称是否与其他标签重复
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tag.getName())
               .ne(Tag::getId, tag.getId());
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("标签名称已存在");
        }

        tagMapper.updateById(tag);
        log.info("更新标签成功，ID: {}", tag.getId());
    }

    @Override
    public void deleteTag(Long id) {
        Tag existingTag = tagMapper.selectById(id);
        if (existingTag == null) {
            throw new RuntimeException("标签不存在");
        }

        // 删除标签关联
        tagMapper.deletePostTags(id);
        
        // 删除标签
        tagMapper.deleteById(id);
        log.info("删除标签成功，ID: {}", id);
    }

    @Override
    public TagVO getTagById(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }

        TagVO tagVO = new TagVO();
        BeanUtils.copyProperties(tag, tagVO);
        return tagVO;
    }

    @Override
    public List<TagVO> getAllTags() {
        List<Tag> tags = tagMapper.selectList(null);
        return tags.stream()
                .map(tag -> {
                    TagVO tagVO = new TagVO();
                    BeanUtils.copyProperties(tag, tagVO);
                    return tagVO;
                })
                .collect(Collectors.toList());
    }
}
