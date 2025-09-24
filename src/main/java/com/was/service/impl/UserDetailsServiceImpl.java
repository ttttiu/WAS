package com.was.service.impl;

import com.was.mapper.AuthMapper;
import com.was.pojo.entity.LoginUser;
import com.was.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AuthMapper authMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //查询数据库
        User user = authMapper.getUser(username);
        //没有查询到用户，抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        //查询到用户，封装成UserDetails返回
        return new LoginUser(user);
    }
}
