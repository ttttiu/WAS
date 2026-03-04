package com.was.service;

import com.was.pojo.dto.ChangePasswordDTO;
import com.was.pojo.dto.UpdateUserInfoDTO;
import com.was.pojo.dto.UserManageDTO;
import com.was.pojo.vo.UserFormVO;
import com.was.pojo.vo.UserManageVO;

import java.util.List;

public interface UserService {

    /**
     * 获取用户表单
     * @return 用户表单
     */
    List<UserFormVO> getUserFrom();

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param updateUserInfoDTO 更新信息
     */
    void updateUserInfo(Long userId, UpdateUserInfoDTO updateUserInfoDTO);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param changePasswordDTO 修改密码信息
     */
    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);
    
    /**
     * 搜索用户
     */
    List<UserManageVO> searchUsers(String userName, String nickname, Long roleId, Integer accountStatus);
    
    /**
     * 更新用户管理信息
     */
    void updateUserManage(UserManageDTO dto);
}
