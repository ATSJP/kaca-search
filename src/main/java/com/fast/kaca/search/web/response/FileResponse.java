package com.fast.kaca.search.web.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.ByteArrayOutputStream;

/**
 * @author sys
 * @date 2019/4/20
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class FileResponse extends BaseResponse {
    private String fileName;
    private ByteArrayOutputStream byteArrayOutputStream;
}
