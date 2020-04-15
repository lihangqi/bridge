package com.bridge.utils;

import com.bridge.domain.BridgeConfig;
import com.bridge.domain.Constants;
import com.bridge.enums.EnvEnum;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.log.LogHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static com.bridge.annotation.BridgeConfigProperties.*;
import static javax.xml.bind.DatatypeConverter.*;

/**
 * @author Jay
 * @version v1.0
 * @description 操作属性相关的工具类
 * @date 2018-12-26 15:18
 */
@Slf4j
public class PropUtils {

    private PropUtils() {
        throw new BridgeProcessFailException(BridgeErrorEnum.NOT_INSTANTIATION_ERROR);
    }


    /**
     * 加载属性文件
     *
     * @param fileName
     * @return
     */
    public static Properties loadProps(String fileName) {
        Properties properties = null;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                throw new BridgeProcessFailException(BridgeErrorEnum.FILE_NOT_EXIST_ERROR);
            }
            properties = new Properties();
            properties.load(is);
        } catch (IOException e) {
            log.error("读取配置文件失败,失败原因为:", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new BridgeProcessFailException(BridgeErrorEnum.STREAM_CLOSE_ERROR);
                }
            }

        }
        return properties;
    }


    /**
     * 加载数据文件
     *
     * @param inputStream
     * @return
     */
    public static Properties loadProps(InputStream inputStream) {
        Properties properties = null;
        try {
            if (inputStream == null) {
                throw new FileNotFoundException("流为空，无法解析");
            }
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("读取配置文件失败,失败原因为:", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭流失败,失败原因为:", e);
                }
            }

        }
        return properties;
    }

    /**
     * 获取字符串属性（可指定默认值）
     *
     * @param props
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Properties props, String key, String defaultValue) {
        String value = defaultValue;
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    /**
     * 类型转换
     *
     * @param clazz
     * @param value
     * @return
     */
    public static Object convertValue(Class<?> clazz, String value) {
        try {
            if (clazz == null || StringUtils.isEmpty(value)) {
                return null;
            }
            value = value.trim();
            if (String.class.equals(clazz)) {
                return value;
            }
            if (Boolean.class.equals(clazz) || Boolean.TYPE.equals(clazz)) {
                if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
                    return Boolean.TRUE;
                }
                if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
                    return Boolean.FALSE;
                }
                throw new RuntimeException();
            }
            if (Short.class.equals(clazz) || Short.TYPE.equals(clazz)) {
                return parseShort(value);
            }
            if (Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)) {
                return parseInt(value);
            }
            if (Long.class.equals(clazz) || Long.TYPE.equals(clazz)) {
                return parseLong(value);
            }
            if (Float.class.equals(clazz) || Float.TYPE.equals(clazz)) {
                return parseFloat(value);
            }
            if (Double.class.equals(clazz) || Double.TYPE.equals(clazz)) {
                return parseDouble(value);
            }
            if (BigDecimal.class.equals(clazz)) {
                return new BigDecimal(value);
            }
        } catch (Exception e) {
            LogHandler.error(String.format("配置项类型转换失败,失败的配置项的值为:「%s」，配置项类型为:「%s」", value, clazz.getName()));
            throw new BridgeProcessFailException(BridgeErrorEnum.CONVERT_ERROR.getCode()
                    , String.format("配置项类型转换失败,失败的配置项的值为:{%s}，配置项类型为:{%s}", value, clazz.getName()));
        }
        throw new BridgeProcessFailException(BridgeErrorEnum.CONVERT_ERROR.getCode(), "未知的类型转换");
    }


    /**
     * 替换属性，如果存在的话
     *
     * @param properties
     * @param propertyResolver
     * @return
     */
    public static Properties resolveByPropertyResolver(Map<?, ?> properties, PropertyResolver propertyResolver) {
        Properties resolvedProperties = new Properties();
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            if (entry.getValue() instanceof CharSequence) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
                String resolvedValue = propertyResolver.resolvePlaceholders(value);
                if (StringUtils.hasText(resolvedValue)) {
                    resolvedProperties.setProperty(key, resolvedValue);
                }
            }
        }
        return resolvedProperties;
    }

    /**
     * 构造{@link BridgeConfig}
     *
     * @return
     */
    public static BridgeConfig buildBridgeConfig(BeanFactory beanFactory) {
        return buildBridgeConfig((Properties) beanFactory.getBean(Constants.BRIDGE_CONFIG_PROPERTIES_BEAN_NAME));
    }


    /**
     * 构造{@link BridgeConfig}
     *
     * @param properties
     * @return
     */
    public static BridgeConfig buildBridgeConfig(Properties properties) {
        BridgeConfig bridgeConfig = new BridgeConfig();
        if (properties == null) {
            throw new BridgeProcessFailException(BridgeErrorEnum.PARSE_PARES_ERROR);
        }
        String appCode = properties.getProperty(APP_CODE);
        if (StringUtils.isEmpty(appCode) || TagUtils.isNormalTag(appCode)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.APP_CODE_PARES_ERROR);
        }
        String serverUrl = properties.getProperty(SERVER_URL);
        if (StringUtils.isEmpty(serverUrl) || TagUtils.isNormalTag(serverUrl)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.SERVER_URL_PARES_ERROR);
        }
        String env = properties.getProperty(ENV_ENUM);
        if (StringUtils.isEmpty(env) || TagUtils.isNormalTag(env)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.ENV_PARES_ERROR);
        }
        String appName = properties.getProperty(APP_NAME);
        if (StringUtils.isEmpty(appName) || TagUtils.isNormalTag(appName)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.ENV_PARES_ERROR);
        }
        EnvEnum envEnum = EnvEnum.isContains(env);
        if (envEnum == null) {
            throw new BridgeProcessFailException(BridgeErrorEnum.ENV_PARES_ERROR);
        }
        bridgeConfig.setEnvEnum(envEnum);
        bridgeConfig.setServerUrl(serverUrl);
        bridgeConfig.setAppCode(appCode);
        bridgeConfig.setAppName(appName);
        return bridgeConfig;
    }


    /**
     * 校验参数合法性
     *
     * @param properties
     * @return
     */
    public static void checkProperties(Properties properties) {
        Object appCode = properties.get(APP_CODE);
        Object serverUrl = properties.get(SERVER_URL);
        Object envEnum = properties.get(ENV_ENUM);
        Object appName = properties.get(APP_NAME);
        check(appCode, serverUrl, envEnum, appName);
    }


    /**
     * 参数校验
     *
     * @param appCode
     * @param serverUrl
     * @param envEnum
     * @param appName
     */
    public static void check(Object appCode, Object serverUrl, Object envEnum, Object appName) {
        if (appCode == null || serverUrl == null || envEnum == null || appName == null) {
            throw new BridgeProcessFailException(BridgeErrorEnum.PARSE_PARES_ERROR);
        }
        if (TagUtils.isNormalTag(envEnum.toString())) {
            throw new BridgeProcessFailException(BridgeErrorEnum.ENV_PARES_ERROR);
        }
        if (TagUtils.isNormalTag(appCode.toString())) {
            throw new BridgeProcessFailException(BridgeErrorEnum.APP_CODE_PARES_ERROR);
        }
        if (TagUtils.isNormalTag(serverUrl.toString())) {
            throw new BridgeProcessFailException(BridgeErrorEnum.SERVER_URL_PARES_ERROR);
        }
        if (TagUtils.isNormalTag(appName.toString())) {
            throw new BridgeProcessFailException(BridgeErrorEnum.APP_NAME_PARES_ERROR);
        }
        EnvEnum env = EnvEnum.isContains(envEnum.toString());
        if (env == null) {
            throw new BridgeProcessFailException(BridgeErrorEnum.ENV_PARES_ERROR);
        }
    }


    /**
     * 检验不为空
     *
     * @param bridgeConfig
     */
    public static void checkBridgeConfigNotNull(BridgeConfig bridgeConfig) {
        if (bridgeConfig == null) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        if (StringUtils.isEmpty(bridgeConfig.getAppCode())) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        if (StringUtils.isEmpty(bridgeConfig.getServerUrl())) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        if (bridgeConfig.getEnvEnum() == null) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
        if (StringUtils.isEmpty(bridgeConfig.getAppName())) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR);
        }
    }

}
