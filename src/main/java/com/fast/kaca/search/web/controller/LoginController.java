package com.fast.kaca.search.web.controller;

import com.fast.kaca.search.web.request.LoginRequest;
import com.fast.kaca.search.web.response.LoginResponse;
import com.fast.kaca.search.web.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author sjp
 * @date 2019/4/16
 **/
@RestController
@RequestMapping("/user")
public class LoginController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "登陆", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@Valid LoginRequest request) {
        LoginResponse response = new LoginResponse();
        userService.login(request, response);
        return response;
    }

}
