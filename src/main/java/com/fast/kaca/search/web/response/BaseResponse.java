package com.fast.kaca.search.web.response;

import com.fast.kaca.search.web.constant.ConstantApi;

import java.io.Serializable;

/**
 * @author sys
 * @date 2019/4/15
 **/
public class BaseResponse implements Serializable {
    /**
     * 用户的唯一id
     */
    private Integer uid;
    /**
     * token
     */
    private String token;
    /**
     * 返回状态码
     */
    private int code = ConstantApi.CODE.SUCCESS.getCode();
    /**
     * 返回描述
     */
    private String msg = ConstantApi.CODE.SUCCESS.getDesc();
    /**
     * 返回数据
     */
    private Object data;

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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
