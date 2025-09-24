package com.was.service.impl;

import com.was.mapper.UserMapper;
import com.was.pojo.vo.UserFormVO;
import com.was.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取用户表单
     * @return 用户表单
     */
    @Override
    public List<UserFormVO> getUserFrom() {
        return userMapper.getUserFrom();
    }
}
