package com.fast.kaca.search.web.request;

/**
 * @author sys
 * @date 2019/4/15
 **/
public class BaseRequest {
    /**
     * 用户的唯一id
     */
    private Integer uid;
    /**
     * token
     */
    private String token;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
