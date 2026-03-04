package com.was.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.was.pojo.entity.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 标签Mapper
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据文章ID查询标签列表
     */
    @Select("SELECT t.* FROM blog_tag t " +
            "INNER JOIN blog_post_tag pt ON t.id = pt.tag_id " +
            "WHERE pt.post_id = #{postId}")
    List<Tag> selectTagsByPostId(Long postId);

    /**
     * 批量插入文章标签关联
     */
    @Insert("<script>" +
            "INSERT INTO blog_post_tag (post_id, tag_id) VALUES " +
            "<foreach collection='tagIds' item='tagId' separator=','>" +
            "(#{postId}, #{tagId})" +
            "</foreach>" +
            "</script>")
    void insertPostTags(Long postId, List<Long> tagIds);

    /**
     * 删除文章的所有标签关联
     */
    @Delete("DELETE FROM blog_post_tag WHERE post_id = #{postId}")
    void deletePostTags(Long postId);
}
