package com.fast.kaca.search.core.utils;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * word工具
 *
 * @author sys
 * @date 2019/4/14
 **/
public class WordUtils {
    private static final Logger logger = LoggerFactory.getLogger(WordUtils.class);
    /**
     * HWPF是操作Microsoft Word 97（-2007）文件的标准API入口。它还支持对旧版Word 6和Word 95文件对有限的只读功能。
     * XWPF是操作Microsoft Word 2007文件的标准API入口。
     */
    private static final String WORD_2007 = ".doc";
    private static final String WORD_2010 = ".docx";

//    WordUtils() throws Exception {
//        if (StringUtils.isEmpty(fileDir)) {
//            logger.error("未配置论文保存路径！ file_path is empty");
//            throw new Exception("未配置论文保存路径！ file_path is empty");
//        }
//    }

    /**
     * 读取word文档
     *
     * @param filePath 文件路径
     * @return 段落切割的list（不含图片）
     */
    public static List<String> readWordFile(String filePath) {
        List<String> contextList = Lists.newArrayList();
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(filePath));
            // 默认都按照段落来读取
            if (filePath.endsWith(WORD_2007)) {
                getParagraphsFromWord2003(contextList, stream);
            } else if (filePath.endsWith(WORD_2010)) {
                getParagraphsFromWord2007(contextList, stream);
            }
        } catch (IOException e) {
            logger.debug("读取word文件失败->e:{}", e.getMessage());
        } finally {
            closeStream(stream);
        }
        return contextList;
    }

    /**
     * 从word2003即老版本，以段落为切割
     *
     * @param contextList 内容list
     * @param stream      流
     * @throws IOException e
     */
    private static void getParagraphsFromWord2003(List<String> contextList, InputStream stream) throws IOException {
        HWPFDocument document = new HWPFDocument(stream);
        WordExtractor extractor = new WordExtractor(document);
        String[] contextArray = extractor.getParagraphText();
        Arrays.asList(contextArray).forEach(context -> contextList.add(CharMatcher.whitespace().removeFrom(context)));
        extractor.close();
        document.close();
    }

    /**
     * 从word2007即新版本，以段落为切割
     *
     * @param contextList 内容list
     * @param stream      流
     * @throws IOException e
     */
    private static void getParagraphsFromWord2007(List<String> contextList, InputStream stream) throws IOException {
        XWPFDocument document = new XWPFDocument(stream).getXWPFDocument();
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        paragraphList.forEach(paragraph -> contextList.add(CharMatcher.whitespace().removeFrom(paragraph.getParagraphText())));
        document.close();
    }

    /**
     * 关闭流
     *
     * @param stream 流
     */
    private static void closeStream(InputStream stream) {
        if (null != stream) {
            try {
                stream.close();
            } catch (IOException e) {
                logger.debug("关闭word文件失败->e:{}", e.getMessage());
            }
        }
    }

}
