package com.was.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.was.pojo.entity.User;
import com.was.pojo.vo.UserFormVO;
import com.was.pojo.vo.UserManageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from sys_user")
    List<UserFormVO> getUserFrom();
    
    @Select("<script>" +
            "SELECT u.*, ur.role_id, r.role_name " +
            "FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE 1=1 " +
            "<if test='userName != null and userName != \"\"'>" +
            "AND u.username LIKE CONCAT('%', #{userName}, '%') " +
            "</if>" +
            "<if test='nickname != null and nickname != \"\"'>" +
            "AND u.nickname LIKE CONCAT('%', #{nickname}, '%') " +
            "</if>" +
            "<if test='roleId != null'>" +
            "AND ur.role_id = #{roleId} " +
            "</if>" +
            "<if test='accountStatus != null'>" +
            "AND u.account_status = #{accountStatus} " +
            "</if>" +
            "ORDER BY u.create_time DESC" +
            "</script>")
    List<UserManageVO> searchUsers(@Param("userName") String userName,
                                    @Param("nickname") String nickname,
                                    @Param("roleId") Long roleId,
                                    @Param("accountStatus") Integer accountStatus);

}
