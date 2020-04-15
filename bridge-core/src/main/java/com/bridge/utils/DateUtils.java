package com.bridge.utils;


import com.bridge.enums.BridgeErrorEnum;
import com.bridge.exception.BridgeProcessFailException;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-09-04 15:52
 */
public class DateUtils {


    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";


    private static final String DATE_FORMAT_FOR_VERSION = "yyyyMMddHHmmssSSS";


    /**
     * 按指定的格式，将日期转换成为字符
     *
     * @param date   日期
     * @param format 格式
     * @return 按指定的格式，将日期转换成为字符
     */
    public static String format(Date date, String format) {
        if (date == null) {
            return "";
        }
        if (StringUtils.isEmpty(format)) {
            format = DATE_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }


    /**
     * 以当前时间精确到毫秒为版本号
     *
     * @return
     */
    public static String getVersion() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_FOR_VERSION);
        return simpleDateFormat.format(date);
    }

    /**
     * 比较2个时间谁更大，String类型的
     *
     * @param dateOne
     * @param dateTwo
     * @return
     */
    public static boolean isBigger(String dateOne, String dateTwo) {
        if (StringUtils.isEmpty(dateOne) || StringUtils.isEmpty(dateTwo)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        return isEarly(getDate(dateOne), getDate(dateTwo));
    }

    /**
     * 获取日期
     *
     * @param date
     * @return
     */
    private static Date getDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_FOR_VERSION);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            throw new BridgeProcessFailException(BridgeErrorEnum.CONVERT_ERROR);
        }
    }


    /**
     * 比较2个时间谁更大
     *
     * @param oneDate
     * @param twoDate
     * @return oneDate大则返回true，否则返回false
     */
    private static boolean isEarly(Date oneDate, Date twoDate) {
        if (oneDate == null || twoDate == null) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        return oneDate.after(twoDate);
    }


}
