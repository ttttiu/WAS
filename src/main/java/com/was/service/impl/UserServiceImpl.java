package com.was.service.impl;

import com.was.mapper.UserMapper;
import com.was.pojo.dto.ChangePasswordDTO;
import com.was.pojo.dto.UpdateUserInfoDTO;
import com.was.pojo.dto.UserManageDTO;
import com.was.pojo.entity.LoginUser;
import com.was.pojo.entity.User;
import com.was.pojo.vo.UserFormVO;
import com.was.pojo.vo.UserManageVO;
import com.was.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取用户表单
     * @return 用户表单
     */
    @Override
    public List<UserFormVO> getUserFrom() {
        return userMapper.getUserFrom();
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param updateUserInfoDTO 更新信息
     */
    @Override
    @Transactional
    public void updateUserInfo(Long userId, UpdateUserInfoDTO updateUserInfoDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        if (updateUserInfoDTO.getNickname() != null) {
            user.setNickname(updateUserInfoDTO.getNickname());
        }
        if (updateUserInfoDTO.getEmail() != null) {
            user.setEmail(updateUserInfoDTO.getEmail());
        }
        if (updateUserInfoDTO.getAvatar() != null) {
            user.setAvatar(updateUserInfoDTO.getAvatar());
        }

        userMapper.updateById(user);
        log.info("用户 {} 更新个人信息成功", userId);

        // 更新 Redis 中的用户信息
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get("login:" + userId);
        if (loginUser != null) {
            loginUser.setUser(user);
            redisTemplate.opsForValue().set("login:" + userId, loginUser);
            log.info("更新 Redis 中的用户信息成功");
        }
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param changePasswordDTO 修改密码信息
     */
    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        String newEncodedPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        user.setPassword(newEncodedPassword);
        userMapper.updateById(user);
        log.info("用户 {} 修改密码成功", userId);

        // 删除 Redis 中的登录信息，强制重新登录
        redisTemplate.delete("login:" + userId);
        log.info("删除 Redis 中的登录信息，用户需要重新登录");
    }

    /**
     * 搜索用户
     */
    @Override
    public List<UserManageVO> searchUsers(String userName, String nickname, Long roleId, Integer accountStatus) {
        return userMapper.searchUsers(userName, nickname, roleId, accountStatus);
    }
    
    /**
     * 更新用户管理信息
     */
    @Override
    @Transactional
    public void updateUserManage(UserManageDTO dto) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新账户状态和禁用原因
        if (dto.getAccountStatus() != null) {
            user.setAccountStatus(dto.getAccountStatus());
            user.setDisableReason(dto.getDisableReason());
            userMapper.updateById(user);
            
            // 如果禁用用户，删除Redis中的登录信息
            if (dto.getAccountStatus() == 1) {
                redisTemplate.delete("login:" + dto.getUserId());
                log.info("用户 {} 被禁用，已删除登录信息", dto.getUserId());
            }
        }
        
        // 更新角色
        if (dto.getRoleId() != null) {
            jdbcTemplate.update("DELETE FROM sys_user_role WHERE user_id = ?", dto.getUserId());
            jdbcTemplate.update("INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)", 
                    dto.getUserId(), dto.getRoleId());
            
            // 删除Redis中的登录信息，强制重新登录以更新权限
            redisTemplate.delete("login:" + dto.getUserId());
            log.info("用户 {} 角色已更新为 {}", dto.getUserId(), dto.getRoleId());
        }
    }
}
