package com.bridge.enums;

import lombok.Getter;

/**
 * @author Jay
 * @version v1.0
 * @description 日志级别
 * @date 2019-08-30 16:22
 */
public enum LogLevelEnum {


    // DEBUG
    DEBUG(0, "DEBUG"),

    // INFO
    INFO(1, "INFO"),

    // WARN
    WARN(2, "WARN"),

    // ERROR
    ERROR(3, "ERROR");


    @Getter
    private int key;

    @Getter
    private String value;

    LogLevelEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 根据key获取对应的枚举类型
     *
     * @param key
     * @return
     */
    public static LogLevelEnum getLogLevelEnum(int key) {
        LogLevelEnum[] enums = LogLevelEnum.values();
        for (LogLevelEnum envEnum : enums) {
            if (envEnum.getKey() == key) {
                return envEnum;
            }
        }
        return null;
    }
}
