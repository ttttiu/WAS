package com.was.controller;

import com.was.annotation.RequirePermission;
import com.was.pojo.Result;
import com.was.pojo.dto.AuthorApplicationDTO;
import com.was.pojo.dto.ReviewApplicationDTO;
import com.was.pojo.entity.LoginUser;
import com.was.pojo.vo.AuthorApplicationVO;
import com.was.service.AuthorApplicationService;
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
@RequestMapping("/api/author-application")
@Slf4j
public class AuthorApplicationController {
    
    @Autowired
    private AuthorApplicationService authorApplicationService;
    
    /**
     * 提交作者申请
     */
    @PostMapping("/submit")
    public Result<Void> submitApplication(@Valid @RequestBody AuthorApplicationDTO dto,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return Result.error(errorMessage);
        }
        
        Long userId = getCurrentUserId();
        authorApplicationService.submitApplication(userId, dto);
        log.info("用户 {} 提交作者申请", userId);
        return Result.success();
    }
    
    /**
     * 获取当前用户的申请状态
     */
    @GetMapping("/my-application")
    public Result<AuthorApplicationVO> getMyApplication() {
        Long userId = getCurrentUserId();
        AuthorApplicationVO application = authorApplicationService.getUserApplication(userId);
        return Result.success(application);
    }
    
    /**
     * 获取待审核的申请列表（管理员）
     */
    @GetMapping("/pending")
    @RequirePermission("system:user:manage")
    public Result<List<AuthorApplicationVO>> getPendingApplications() {
        List<AuthorApplicationVO> applications = authorApplicationService.getPendingApplications();
        return Result.success(applications);
    }
    
    /**
     * 获取所有申请列表（管理员）
     */
    @GetMapping("/all")
    @RequirePermission("system:user:manage")
    public Result<List<AuthorApplicationVO>> getAllApplications() {
        List<AuthorApplicationVO> applications = authorApplicationService.getAllApplications();
        return Result.success(applications);
    }
    
    /**
     * 审核申请（管理员）
     */
    @PostMapping("/review")
    @RequirePermission("system:user:manage")
    public Result<Void> reviewApplication(@Valid @RequestBody ReviewApplicationDTO dto,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return Result.error(errorMessage);
        }
        
        Long reviewerId = getCurrentUserId();
        authorApplicationService.reviewApplication(reviewerId, dto);
        log.info("管理员 {} 审核申请 {}", reviewerId, dto.getApplicationId());
        return Result.success();
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser().getId();
    }
}
