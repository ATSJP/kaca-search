package com.fast.kaca.search.web.controller;

import com.fast.kaca.search.web.request.LoginRequest;
import com.fast.kaca.search.web.response.LoginResponse;
import com.fast.kaca.search.web.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 用户登陆
 *
 * @author sys
 * @date 2019/4/16
 **/
@RestController
@RequestMapping("/user")
public class LoginController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "注册", notes = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String")
    })
    @PutMapping(value = "/register")
    public LoginResponse register(@Valid LoginRequest request) {
        LoginResponse response = new LoginResponse();
        userService.register(request, response);
        return response;
    }

    @ApiOperation(value = "登陆", notes = "登陆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String")
    })
    @PostMapping(value = "/login")
    public LoginResponse login(@Valid LoginRequest request) {
        LoginResponse response = new LoginResponse();
        userService.login(request, response);
        return response;
    }

    @ApiOperation(value = "退出", notes = "退出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
    })
    @DeleteMapping(value = "/logout")
    public LoginResponse logout(LoginRequest request) {
        LoginResponse response = new LoginResponse();
        userService.logout(request, response);
        return response;
    }

}
