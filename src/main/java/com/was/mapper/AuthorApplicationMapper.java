package com.was.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.was.pojo.entity.AuthorApplication;
import com.was.pojo.vo.AuthorApplicationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuthorApplicationMapper extends BaseMapper<AuthorApplication> {
    
    @Select("SELECT aa.*, u.username, u.nickname, r.username as reviewer_name " +
            "FROM author_application aa " +
            "LEFT JOIN sys_user u ON aa.user_id = u.id " +
            "LEFT JOIN sys_user r ON aa.reviewer_id = r.id " +
            "WHERE aa.status = #{status} " +
            "ORDER BY aa.create_time DESC")
    List<AuthorApplicationVO> getApplicationsByStatus(@Param("status") Integer status);
    
    @Select("SELECT aa.*, u.username, u.nickname, r.username as reviewer_name " +
            "FROM author_application aa " +
            "LEFT JOIN sys_user u ON aa.user_id = u.id " +
            "LEFT JOIN sys_user r ON aa.reviewer_id = r.id " +
            "ORDER BY aa.create_time DESC")
    List<AuthorApplicationVO> getAllApplications();
}
