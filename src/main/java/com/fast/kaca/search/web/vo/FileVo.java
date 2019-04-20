package com.fast.kaca.search.web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author sys
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
    /**
     * 上传作者
     */
    private String createName;
    /**
     * 上传时间
     */
    private Timestamp createTime;
}
