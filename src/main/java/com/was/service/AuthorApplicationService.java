package com.was.service;

import com.was.pojo.dto.AuthorApplicationDTO;
import com.was.pojo.dto.ReviewApplicationDTO;
import com.was.pojo.vo.AuthorApplicationVO;

import java.util.List;

public interface AuthorApplicationService {
    
    void submitApplication(Long userId, AuthorApplicationDTO dto);
    
    List<AuthorApplicationVO> getPendingApplications();
    
    List<AuthorApplicationVO> getAllApplications();
    
    void reviewApplication(Long reviewerId, ReviewApplicationDTO dto);
    
    AuthorApplicationVO getUserApplication(Long userId);
}
