package com.fast.kaca.search.web.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author sys
 * @date 2019/4/20
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class FileRequest extends BaseRequest {
    @NotNull
    private Integer fileId;
    /**
     * 是否拿取原文件 0 否(获取查重后的文件) 1 是
     */
    @NotNull
    private Short isSource;
}
