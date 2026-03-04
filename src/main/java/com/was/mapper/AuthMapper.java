package com.was.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.was.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthMapper extends BaseMapper<User> {

//    @Insert("insert into sys_user(username,password,name,create_time) values(#{username},#{password},#{name},#{createTime})")
    @Insert("insert into sys_user(username, password, email) VALUES (#{userName},#{password},#{email})")
    void insertUser(User user);

    @Select("select * from sys_user where username = #{userName}")
    User getUser(String userName);
}
