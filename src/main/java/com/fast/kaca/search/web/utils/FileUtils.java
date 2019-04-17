package com.fast.kaca.search.web.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

    public static void writeFile(String filePath, String fileName, byte[] content) throws Exception {
        File file = new File(filePath + fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        FileChannel channel = outputStream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        while(){
            // 将postion置0，limit变成capacity，迎接下一次读取
            buffer.clear();
            buffer.put(content);
            buffer.flip();
            channel.write(buffer);
//        }
        channel.close();
        outputStream.close();
    }
}
