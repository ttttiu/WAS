package com.was.controller;


import com.was.pojo.Result;
import com.was.pojo.vo.UserFormVO;
import com.was.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
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

}
