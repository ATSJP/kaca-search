package com.fast.kaca.search.web.service;

import com.fast.kaca.search.web.constant.ConstantApi;
import com.fast.kaca.search.web.dao.UserDao;
import com.fast.kaca.search.web.entity.UserEntity;
import com.fast.kaca.search.web.request.LoginRequest;
import com.fast.kaca.search.web.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sjp
 * @date 2019/4/16
 **/
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void login(LoginRequest request, LoginResponse response) {
        String userName = request.getUserName();
        String password = request.getPassword();
        UserEntity userEntity = userDao.findByUserName(userName);
        if (userEntity == null) {
            response.setCode(ConstantApi.LOGIN_MESSAGE.ERROR.getCode());
            response.setMsg(ConstantApi.LOGIN_MESSAGE.ERROR.getDesc());
            return;
        }
        if (!password.equals(userEntity.getPassword())) {
            response.setCode(ConstantApi.LOGIN_MESSAGE.ERROR.getCode());
            response.setMsg(ConstantApi.LOGIN_MESSAGE.ERROR.getDesc());
            return;
        }
        response.setCode(ConstantApi.LOGIN_MESSAGE.SUCCESS.getCode());
        response.setMsg(ConstantApi.LOGIN_MESSAGE.SUCCESS.getDesc());
    }

}
