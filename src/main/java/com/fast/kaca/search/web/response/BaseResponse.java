package com.fast.kaca.search.web.response;

import com.fast.kaca.search.web.constant.ConstantApi;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sys
 * @date 2019/4/15
 **/
@Data
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
    private Short code = ConstantApi.CODE.SUCCESS.getCode();
    /**
     * 返回描述
     */
    private String msg = ConstantApi.CODE.SUCCESS.getDesc();
    /**
     * 返回数据
     */
    private Object data;
}
