package com.was.mapper;

import com.was.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthMapper {

//    @Insert("insert into user(username,password,name,create_time) values(#{username},#{password},#{name},#{createTime})")
    @Insert("insert into user(userName, password, email) VALUES (#{userName},#{password},#{email})")
    void insertUser(User user);

    @Select("select * from user where userName = #{userName}")
    User getUser(String userName);
}
