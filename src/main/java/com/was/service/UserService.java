package com.was.service;

import com.was.pojo.vo.UserFormVO;

import java.util.List;

public interface UserService {

    /**
     * 获取用户表单
     * @return 用户表单
     */
    List<UserFormVO> getUserFrom();
}
