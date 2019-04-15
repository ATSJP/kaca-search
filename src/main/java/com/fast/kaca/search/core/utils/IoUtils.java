package com.fast.kaca.search.core.utils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * @author sys
 * @date 2019/4/14
 **/
public class IoUtils {

    private static Logger logger = LoggerFactory.getLogger(IoUtils.class);

    /**
     * 定义分词器
     */
    private static final Analyzer ANALYZER = new IKAnalyzer();

//    IoUtils() throws Exception {
//        if (StringUtils.isEmpty(config.getIndexDir())) {
//            logger.error("未配置Lucene索引文件路径！ index_path is empty");
//            throw new Exception("未配置Lucene索引文件路径！ index_path is empty");
//        }
//    }

    /**
     * 封裝一个方法，用于将数据库中的数据解析为一个个关键字词存储到索引文件中
     *
     * @param doc doc
     */
    public static void write(Document doc, String path) throws IOException {
        //索引库的存储目录
        Directory directory = FSDirectory.open(new File(path));
        //关联当前lucene版本和分值器
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, ANALYZER);
        //传入目录和分词器
        IndexWriter indexWriter = new IndexWriter(directory, config);
        indexWriter.commit();
        //写入到目录文件中
        indexWriter.addDocument(doc);
        //提交事务
        indexWriter.commit();
        //关闭流
        indexWriter.close();
    }

}
