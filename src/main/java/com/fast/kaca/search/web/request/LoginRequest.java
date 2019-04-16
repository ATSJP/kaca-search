package com.fast.kaca.search.web.request;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author sys
 * @date 2019/4/16
 **/
public class LoginRequest extends BaseRequest {
    /**
     * 用户名
     */
    @NotBlank
    @Length(min = 1, max = 50)
    private String userName;
    /**
     * 密码
     */
    @NotBlank
    @Length(min = 1, max = 100)
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
