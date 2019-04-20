package com.fast.kaca.search.web.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * @author sys
 * @date 2019/4/15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchRequest extends BaseRequest {
    @NotBlank
    private String key;
    /**
     * 获取文件list: 0 拿自己的 1 获取库文件
     */
    private Short isListAll;
    /**
     * 上传的文件
     */
    private MultipartFile[] files;
}
