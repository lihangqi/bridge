package com.bridge.console.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author Jay
 * @version v1.0
 * @description MD5加密
 * @date 2018-01-15 18:05
 */
@Slf4j
public class EncryptUtil {


    /**
     * MD5 加密
     *
     * @param str
     * @return
     */
    public static String getMd5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException caught!", e);
            return null;
        } catch (UnsupportedEncodingException e) {
            log.error("MD5加密失败", e);
            return null;
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }
        return md5StrBuff.toString();
    }

    /**
     * 获取token
     *
     * @param str
     * @return
     */
    public static String getToken(String str) {
        return new StringBuilder(getMd5Str(str))
                .insert(8, "-")
                .insert(17, "-")
                .insert(26, "-")
                .toString();
    }


    /**
     * 生成appCode
     *
     * @return
     */
    public static String getAppCode() {
        String str = getMd5Str(String.valueOf(System.currentTimeMillis())).substring(0, 16);
        return new StringBuilder(str)
                .insert(4, "-")
                .insert(9, "-")
                .insert(14, "-")
                .toString();
    }
}
