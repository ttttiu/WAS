package com.was.service;

import com.was.pojo.Result;
import com.was.pojo.dto.LoginDTO;
import com.was.pojo.dto.RegisterDTO;
import com.was.pojo.vo.LoginVO;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    // 注册
    void register(RegisterDTO registerDTO);

    // 登录
    Result<LoginVO> login(LoginDTO loginDTO, HttpServletResponse response);

    // 登出
    Result<Void> logout();
}
