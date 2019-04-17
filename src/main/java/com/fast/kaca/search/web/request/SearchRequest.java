package com.fast.kaca.search.web.request;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * @author sys
 * @date 2019/4/15
 **/
public class SearchRequest extends BaseRequest {
    @NotBlank
    private String key;

    private MultipartFile[] files;

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
