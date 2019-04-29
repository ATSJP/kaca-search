package com.fast.kaca.search.web.utils;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sys
 * @date 2019/4/14
 **/
public class FileUtils {

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
     * 获取指定路径的文件byre数组
     * @param filePath 文件路径
     * @return byte[]
     * @throws IOException
     */
    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc;
        while ((rc = inputStream.read(buff, 0, 1024)) > 0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }

}
