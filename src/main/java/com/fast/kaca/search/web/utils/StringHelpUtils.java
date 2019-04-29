package com.fast.kaca.search.web.utils;

import com.google.common.base.Splitter;
import org.springframework.util.StringUtils;

/**
 * @author sjp
 * @date 2019/4/29
 **/
public class StringHelpUtils {

    /**
     * 移除文件名的后缀
     *
     * @param fileName 文件名
     * @return string 不含后缀
     */
    public static String removeSuffix(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return fileName;
        }
        if (fileName.contains(".")) {
            return Splitter.on('.')
                    .trimResults()
                    .omitEmptyStrings()
                    .split(fileName).iterator().next();
        }
        return "";
    }

}
