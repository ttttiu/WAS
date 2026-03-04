package com.was.service.impl;

import com.was.mapper.AuthMapper;
import com.was.mapper.PermissionMapper;
import com.was.pojo.entity.LoginUser;
import com.was.pojo.entity.Permission;
import com.was.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// 用户详情服务实现类
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //查询数据库
        User user = authMapper.getUser(username);
        //没有查询到用户，抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        
        // 检查账户是否被禁用
        if (user.getAccountStatus() != null && user.getAccountStatus() == 1) {
            String reason = user.getDisableReason() != null ? user.getDisableReason() : "账户已被禁用";
            throw new RuntimeException("账户已被禁用: " + reason);
        }

        // 查询用户权限
        List<Permission> permissions = permissionMapper.selectPermissionsByUserId(user.getId());
        List<GrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermissionKey()))
                .collect(Collectors.toList());

        //查询到用户，封装成UserDetails返回
        return new LoginUser(user, authorities);
    }
}
