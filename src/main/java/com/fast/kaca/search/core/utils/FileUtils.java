package com.fast.kaca.search.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sys
 * @date 2019/4/14
 **/
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(IoUtils.class);

    /**
     * 读取论文保存目录下，所有的论文名称
     *
     * @return list<String> 论文名称
     */
    public static List<String> readFileContentList(String fileDir) {
        File file = new File(fileDir);
        // TODO 待优化:不应拿出全部文件内容，只拿出文件即可，防止内存溢出
        File[] files = file.listFiles();
        if (files == null || files.length < 1) {
            return Collections.emptyList();
        }
        List<String> fileNameList = new LinkedList<>();
        for (File item : files) {
            String name = item.getName();
            fileNameList.add(name);
        }
        return fileNameList;
    }

    /**
     * 读取论文文件
     *
     * @return 论文内容
     */
    public static File readFile(String fileDir) {
        File file = new File(fileDir);
        if (!file.exists()) {
            return null;
        }
        return file;
    }

}
