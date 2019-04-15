package com.fast.kaca.search.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sjp
 * @date 2019/4/15
 **/
@Component
public class Config {
    /**
     * 论文保存路径
     */
    @Value("${file_path}")
    private String fileDir;
    /**
     * Lucene索引文件路径
     */
    @Value("${index_path}")
    private String indexDir = "";
    /**
     * 论文保存路径
     */
    @Value("${text_length}")
    private Integer textLength;

    public String getFileDir() {
        return fileDir;
    }

    public String getIndexDir() {
        return indexDir;
    }

    public Integer getTextLength() {
        return textLength;
    }
}
