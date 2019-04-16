package com.fast.kaca.search.web.request;

import javax.validation.constraints.NotBlank;

/**
 * @author sys
 * @date 2019/4/15
 **/
public class SearchRequest extends BaseRequest {
    @NotBlank
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
