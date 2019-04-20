package com.fast.kaca.search.web.request;

import lombok.Data;

/**
 * @author sys
 * @date 2019/4/15
 **/
@Data
class BaseRequest {
    /**
     * 用户的唯一id
     */
    private Integer uid;
    /**
     * token
     */
    private String token;
}
