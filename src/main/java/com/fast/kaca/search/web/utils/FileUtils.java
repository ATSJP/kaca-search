package com.fast.kaca.search.web.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
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

//    public static void writeFile(String filePath, String fileName, byte[] content) throws Exception {
//        File file = new File(filePath + fileName);
//        FileOutputStream outputStream = new FileOutputStream(file);
//        FileChannel channel = outputStream.getChannel();
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
////        while(){
//            // 将postion置0，limit变成capacity，迎接下一次读取
//            buffer.clear();
//            buffer.put(content);
//            buffer.flip();
//            channel.write(buffer);
////        }
//        channel.close();
//        outputStream.close();
//    }

    public byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }

    /**
     * the traditional io way
     *
     */
    public static byte[] toByteArray(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length())) {
            BufferedInputStream in = null;
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static byte[] toByteArrayNIO(String filePath) throws IOException {
        File f = new File(filePath);
        if (!f.exists()) {
            throw new FileNotFoundException(filePath);
        }
        FileChannel channel = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
     *
     */
    public static byte[] toByteArray3(String filename) throws IOException {
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(filename, "r").getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size()).load();
            System.out.println(byteBuffer.isLoaded());
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
