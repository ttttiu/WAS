package com.was.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.was.mapper.AuthorApplicationMapper;
import com.was.mapper.RoleMapper;
import com.was.pojo.dto.AuthorApplicationDTO;
import com.was.pojo.dto.ReviewApplicationDTO;
import com.was.pojo.entity.AuthorApplication;
import com.was.pojo.entity.Role;
import com.was.pojo.vo.AuthorApplicationVO;
import com.was.service.AuthorApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AuthorApplicationServiceImpl implements AuthorApplicationService {
    
    @Autowired
    private AuthorApplicationMapper authorApplicationMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional
    public void submitApplication(Long userId, AuthorApplicationDTO dto) {
        // 检查是否已有待审核的申请
        LambdaQueryWrapper<AuthorApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthorApplication::getUserId, userId)
               .eq(AuthorApplication::getStatus, 0);
        
        AuthorApplication existing = authorApplicationMapper.selectOne(wrapper);
        if (existing != null) {
            throw new RuntimeException("您已有待审核的申请，请勿重复提交");
        }
        
        // 检查用户是否已经是作者
        List<Role> roles = roleMapper.selectRolesByUserId(userId);
        boolean isAuthor = roles.stream().anyMatch(role -> "AUTHOR".equals(role.getRoleKey()));
        if (isAuthor) {
            throw new RuntimeException("您已经是作者，无需申请");
        }
        
        AuthorApplication application = AuthorApplication.builder()
                .userId(userId)
                .applicationReason(dto.getApplicationReason())
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        
        authorApplicationMapper.insert(application);
        log.info("用户 {} 提交作者申请", userId);
    }
    
    @Override
    public List<AuthorApplicationVO> getPendingApplications() {
        return authorApplicationMapper.getApplicationsByStatus(0);
    }
    
    @Override
    public List<AuthorApplicationVO> getAllApplications() {
        return authorApplicationMapper.getAllApplications();
    }
    
    @Override
    @Transactional
    public void reviewApplication(Long reviewerId, ReviewApplicationDTO dto) {
        AuthorApplication application = authorApplicationMapper.selectById(dto.getApplicationId());
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        
        if (application.getStatus() != 0) {
            throw new RuntimeException("该申请已被处理");
        }
        
        application.setStatus(dto.getStatus());
        application.setReviewReason(dto.getReviewReason());
        application.setReviewerId(reviewerId);
        application.setUpdateTime(LocalDateTime.now());
        
        authorApplicationMapper.updateById(application);
        
        // 如果批准，将用户角色改为作者
        if (dto.getStatus() == 1) {
            // 查找作者角色ID
            LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.eq(Role::getRoleKey, "AUTHOR");
            Role authorRole = roleMapper.selectOne(roleWrapper);
            
            if (authorRole != null) {
                // 删除旧角色
                jdbcTemplate.update("DELETE FROM sys_user_role WHERE user_id = ?", application.getUserId());
                // 添加作者角色
                jdbcTemplate.update("INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)", 
                        application.getUserId(), authorRole.getId());
                log.info("用户 {} 成为作者", application.getUserId());
            }
        }
        
        log.info("审核人 {} 处理申请 {}, 结果: {}", reviewerId, dto.getApplicationId(), 
                dto.getStatus() == 1 ? "批准" : "拒绝");
    }
    
    @Override
    public AuthorApplicationVO getUserApplication(Long userId) {
        LambdaQueryWrapper<AuthorApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthorApplication::getUserId, userId)
               .orderByDesc(AuthorApplication::getCreateTime)
               .last("LIMIT 1");
        
        AuthorApplication application = authorApplicationMapper.selectOne(wrapper);
        if (application == null) {
            return null;
        }
        
        List<AuthorApplicationVO> list = authorApplicationMapper.getAllApplications();
        return list.stream()
                .filter(vo -> vo.getId().equals(application.getId()))
                .findFirst()
                .orElse(null);
    }
}
