package com.bridge.utils;

import com.bridge.exception.BridgeProcessFailException;
import com.bridge.enums.BridgeErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author Jay
 * @version v1.0
 * @description xml相关的工具类
 * @date 2018-12-28 10:36
 */
@Slf4j
public class TagUtils {

    private TagUtils() {
        throw new BridgeProcessFailException(BridgeErrorEnum.NOT_INSTANTIATION_ERROR);
    }

    /**
     * 配置中心xml中的标签标识符开始符号，其中T表示动态刷新，F表示不需要动态刷新
     */
    public static final String PREFIX = "$>>{";

    /**
     * 配置中心xml中的标签标识符开始符号，其中T表示动态刷新，F表示不需要动态刷新
     */
    public static final String PREFIX_T = "$>>T{";


    /**
     * 配置中心xml中的标签标识符开始符号，其中T表示动态刷新，F表示不需要动态刷新
     */
    public static final String PREFIX_F = "$>>F{";


    /**
     * 注解或xml中标签标识符结尾符号
     */
    public static final String SUFFIX = "}";


    /**
     * 注解或xml中的标签标识符开始符号
     */
    private static final String NORMAL_SUFFIX = "${";


    /**
     * 使用配置中心的xml的tag校验
     *
     * @param key
     * @return
     */
    public static boolean isBridgeXmlTag(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        if (key.startsWith(PREFIX) && key.endsWith(SUFFIX)) {
            return true;
        }
        if (key.startsWith(PREFIX_T) && key.endsWith(SUFFIX)) {
            return true;
        }
        if (key.startsWith(PREFIX_F) && key.endsWith(SUFFIX)) {
            return true;
        }
        return false;
    }

    /**
     * 解析$>>{key}中的key
     *
     * @param key
     * @return
     */
    public static String bridgeKeyParse(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        if (key.startsWith(PREFIX) && key.endsWith(SUFFIX)) {
            return key.substring(PREFIX.length(), key.length() - SUFFIX.length());
        }
        if (key.startsWith(PREFIX_T) && key.endsWith(SUFFIX)) {
            return key.substring(PREFIX_T.length(), key.length() - SUFFIX.length());
        }
        if (key.startsWith(PREFIX_F) && key.endsWith(SUFFIX)) {
            return key.substring(PREFIX_F.length(), key.length() - SUFFIX.length());
        }
        return null;
    }


    /**
     * $>>{}是否为自动刷新
     *
     * @param key
     * @return
     */
    public static boolean isBridgeXmlTagKeyRefresh(String key) {
        if (!isBridgeXmlTag(key)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR.getCode(), "占位符不合法");
        }
        if (key.startsWith(PREFIX) && key.endsWith(SUFFIX)) {
            return false;
        }
        if (key.startsWith(PREFIX_T) && key.endsWith(SUFFIX)) {
            return true;
        }
        if (key.startsWith(PREFIX_F) && key.endsWith(SUFFIX)) {
            return false;
        }
        throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR.getCode(), "占位符不合法,注意检查是否存在空格");
    }

    /**
     * 使用${}的xml的tag校验
     *
     * @param key
     * @return
     */
    public static boolean isNormalTag(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        return key.startsWith(NORMAL_SUFFIX) && key.endsWith(SUFFIX);
    }

    /**
     * 解析${key} 或 key
     *
     * @param key
     * @return
     */
    public static String keyParse(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        key = key.replace(" ", "");
        if (!isNormalTag(key)) {
            return key;
        }
        return key.substring(NORMAL_SUFFIX.length(), key.length() - SUFFIX.length());
    }

}
