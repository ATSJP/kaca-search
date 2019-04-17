package com.fast.kaca.search.web.utils;

import com.fast.kaca.search.web.config.ConfigProperties;
import com.fast.kaca.search.web.constant.ConstantSystem;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * @author sys
 * @date 2019/4/14
 **/
@Component
public class LuceneTool {

    @Autowired
    private ConfigProperties configProperties;

    /**
     * 定义分词器
     */
    private static final Analyzer ANALYZER = new IKAnalyzer();

    /**
     * 封裝一个方法，用于将数据库中的数据解析为一个个关键字词存储到索引文件中
     *
     * @param docs docs
     */
    public void write(Collection<Document> docs, String path) throws IOException {
        //索引库的存储目录
        Directory directory = FSDirectory.open(new File(path));
        //关联当前lucene版本和分值器
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, ANALYZER);
        if (ConstantSystem.ENV.DEV.getDesc().equals(configProperties.getEnv())) {
            // 设置打开方式：OpenMode.APPEND 会在索引库的基础上追加新索引。OpenMode.CREATE会先清空原来数据，再提交新的索引
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        }
        //传入目录和分词器
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //写入到目录文件中
        indexWriter.addDocuments(docs);
        //提交事务
        indexWriter.commit();
        //关闭流
        indexWriter.close();
    }

}
