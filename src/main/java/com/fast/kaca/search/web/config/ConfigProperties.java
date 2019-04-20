package com.fast.kaca.search.web.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sys
 * @date 2019/4/15
 **/
@Data
@EqualsAndHashCode()
@Component
public class ConfigProperties {
    /**
     * 系统环境 dev 开发环境 prod 生产环境
     */
    @Value("${spring.profiles.active}")
    private String env;
    /**
     * Lucene索引文件路径
     */
    @Value("${index_path}")
    private String indexDir = "";
    /**
     * 论文保存路径
     */
    @Value("${file_source_path}")
    private String fileSourceDir;
    /**
     * 论文处理结果路径
     */
    @Value("${file_result_path}")
    private String fileResultDir;
    /**
     * 论文切割粒度
     */
    @Value("${text_length}")
    private Integer textLength;
}
