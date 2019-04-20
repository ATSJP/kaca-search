package com.fast.kaca.search.web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author sjp
 * @date 2019/4/20
 **/
@Data
@EqualsAndHashCode()
public class FileVo implements Serializable {
    /**
     * 文件id
     */
    private int id;
    /**
     * 文件名称
     */
    private String fileName;
}
