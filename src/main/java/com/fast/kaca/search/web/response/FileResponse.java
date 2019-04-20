package com.fast.kaca.search.web.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.io.ByteArrayResource;

/**
 * @author sjp
 * @date 2019/4/20
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class FileResponse extends BaseResponse {
    private String fileName;
    private ByteArrayResource byteArrayResource;
}
