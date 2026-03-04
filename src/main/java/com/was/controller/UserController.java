package com.was.controller;


import com.was.annotation.RequirePermission;
import com.was.pojo.Result;
import com.was.pojo.dto.ChangePasswordDTO;
import com.was.pojo.dto.UpdateUserInfoDTO;
import com.was.pojo.dto.UserManageDTO;
import com.was.pojo.entity.LoginUser;
import com.was.pojo.vo.UserFormVO;
import com.was.pojo.vo.UserManageVO;
import com.was.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping("/home")
    public Result<String> home() {
        System.out.println("home");
        return Result.success("hello");
    }

    @GetMapping("/form")
    public Result<List<UserFormVO>> getUserFrom() {
        log.info("获取用户表单");
        List<UserFormVO> userForm = userService.getUserFrom();
        return Result.success(userForm);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@Valid @RequestBody UpdateUserInfoDTO updateUserInfoDTO,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return Result.error(errorMessage);
        }

        Long userId = getCurrentUserId();
        userService.updateUserInfo(userId, updateUserInfoDTO);
        log.info("用户 {} 更新个人信息成功", userId);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return Result.error(errorMessage);
        }

        Long userId = getCurrentUserId();
        userService.changePassword(userId, changePasswordDTO);
        log.info("用户 {} 修改密码成功", userId);
        return Result.success();
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser().getId();
    }

    /**
     * 搜索用户（管理员）
     */
    @GetMapping("/search")
    @RequirePermission("system:user:manage")
    public Result<List<UserManageVO>> searchUsers(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Integer accountStatus) {
        log.info("搜索用户: userName={}, nickname={}, roleId={}, accountStatus={}", 
                userName, nickname, roleId, accountStatus);
        List<UserManageVO> users = userService.searchUsers(userName, nickname, roleId, accountStatus);
        return Result.success(users);
    }
    
    /**
     * 更新用户管理信息（管理员）
     */
    @PutMapping("/manage")
    @RequirePermission("system:user:manage")
    public Result<Void> updateUserManage(@RequestBody UserManageDTO dto) {
        log.info("更新用户管理信息: {}", dto);
        userService.updateUserManage(dto);
        return Result.success();
    }

}
