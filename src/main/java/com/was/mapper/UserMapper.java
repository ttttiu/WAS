package com.was.mapper;

import com.was.pojo.vo.UserFormVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user")
    List<UserFormVO> getUserFrom();

}
