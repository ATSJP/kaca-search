package com.fast.kaca.search.web.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Token生成器
 *
 * @author sys
 * @date 2019/2/25
 **/
public class TokenGenerate {

    /**
     * 加密key
     */
    private static final String KEY = "KACA_API";

    /**
     * 获取token
     *
     * @param userName 用户名
     * @param password 密码
     * @return token
     */
    public static String getToken(String userName, String password) {
        StringBuffer sb = new StringBuffer();
        sb.append(System.currentTimeMillis());
        sb.append(userName);
        sb.append(password);
        sb.append(KEY);
        return DigestUtils.md5Hex(sb.toString()).toUpperCase();
    }

}
