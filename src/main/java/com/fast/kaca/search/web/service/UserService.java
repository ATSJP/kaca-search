package com.fast.kaca.search.web.service;

import com.fast.kaca.search.web.constant.ConstantApi;
import com.fast.kaca.search.web.constant.ConstantCache;
import com.fast.kaca.search.web.dao.UserDao;
import com.fast.kaca.search.web.entity.UserEntity;
import com.fast.kaca.search.web.request.LoginRequest;
import com.fast.kaca.search.web.response.LoginResponse;
import com.fast.kaca.search.web.utils.RedissonTools;
import com.fast.kaca.search.web.utils.TokenGenerate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author sys
 * @date 2019/4/16
 **/
@Service
public class UserService {

    @Resource
    private UserDao userDao;
    @Resource
    private RedissonTools redissonTools;

    public void login(LoginRequest request, LoginResponse response) {
        String userName = request.getUserName();
        String password = request.getPassword();
        UserEntity userEntity = userDao.findByUserName(userName);
        if (userEntity == null) {
            response.setCode(ConstantApi.CODE.FAIL.getCode());
            response.setMsg(ConstantApi.LOGIN_MESSAGE.ERROR.getDesc());
            return;
        }
        if (!password.equals(userEntity.getPassword())) {
            response.setCode(ConstantApi.CODE.FAIL.getCode());
            response.setMsg(ConstantApi.LOGIN_MESSAGE.ERROR.getDesc());
            return;
        }
        // 生成token
        String token = TokenGenerate.getToken(userName, password);
        redissonTools.set("token-" + userEntity.getId(), token, ConstantCache.LOGIN_TOKEN_TIME_OUT);
        response.setUid(userEntity.getId());
        response.setToken(token);
        response.setCode(ConstantApi.CODE.SUCCESS.getCode());
        response.setMsg(ConstantApi.LOGIN_MESSAGE.SUCCESS.getDesc());
    }

    public void logout(LoginRequest request, LoginResponse response) {
        redissonTools.delete("token-" + request.getUid());
        response.setCode(ConstantApi.CODE.SUCCESS.getCode());
        response.setMsg(ConstantApi.CODE.SUCCESS.getDesc());
    }
}
