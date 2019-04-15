package com.fast.kaca.search.web.service;

import com.fast.kaca.search.web.config.Config;
import com.fast.kaca.search.web.utils.FileUtils;
import com.fast.kaca.search.web.utils.IoUtils;
import com.fast.kaca.search.web.utils.WordUtils;
import com.google.common.base.Splitter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author sys
 * @date 2019/4/14
 **/
@Service
public class SearchService {

    private Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Resource
    private Config config;

    /**
     * 生成索引
     */
    public void createIndex() {
        // 获取所有要生成索引的文件名
        List<String> fileNameList = FileUtils.readFileContentList(config.getFileDir());
        if (CollectionUtils.isEmpty(fileNameList)) {
            logger.info("there is nothing");
            return;
        }
        fileNameList.forEach(item -> {
            logger.info("read article to create index start->fileName:{}", item);
            long start = System.currentTimeMillis();
            // 读取文件
            logger.info("readWordFile start->fileName:{}", item);
            long start1 = System.currentTimeMillis();
            List<String> paragraphList = WordUtils.readWordFile(config.getFileDir() + "\\" + item);
            long end1 = System.currentTimeMillis();
            logger.info("readWordFile end->fileName:{},time(ms):{}", item, (end1 - start1));
            if (CollectionUtils.isEmpty(paragraphList)) {
                logger.info("file is all of space or null->fileName:{}", item);
            } else {
                logger.info("create index start->fileName:{}", item);
                long start2 = System.currentTimeMillis();
                paragraphList.forEach(paragraph -> {
                    Iterable<String> result = Splitter.fixedLength(config.getTextLength()).trimResults()
                            .split(paragraph);
                    // 将文件分段，每指定配置字数为一段
                    for (String str : result) {
                        // 每段建立索引 并保存索引
                        boolean isSuccess = initIndex(item, str);
                        if (!isSuccess) {
                            logger.info("initIndex error->item:{}", item);
                        }
                    }
                });
                long end2 = System.currentTimeMillis();
                logger.info("create index end->fileName:{},create index end(ms):{}", item, (end2 - start2));
            }
            long end = System.currentTimeMillis();
            logger.info("read article to create index end->fileName:{},timeSpend(ms):{}", item, (end - start));
        });
    }

    private boolean initIndex(String articleName, String text) {
        //创建Document对象
        Document doc = new Document();
        //获取每列数据
        Field articleNameField = new Field("articleName", articleName, TextField.TYPE_STORED);
        Field textField = new Field("text", text, TextField.TYPE_STORED);
        articleNameField.setBoost(4f);
        //添加到Document中
        doc.add(articleNameField);
        doc.add(textField);
        //调用，创建索引库
        try {
            IoUtils.write(doc, config.getIndexDir());
        } catch (IOException e) {
            logger.error("write doc error:{}", e.getMessage());
            return false;
        }
        return true;
    }


}
