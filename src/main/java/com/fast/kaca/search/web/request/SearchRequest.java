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
     * 是否展示所有已上传文件 0 否 1 是
     */
    private Short isListAll;
    /**
     * 是否获取库文件 0 否 1 是
     */
    private Short isListFileData;
    /**
     * 上传的文件
     */
    private MultipartFile[] files;
}
