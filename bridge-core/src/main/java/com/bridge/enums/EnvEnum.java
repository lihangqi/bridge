package com.bridge.enums;

import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * @author Jay
 * @version v1.0
 * @description 环境的枚举
 * @date 2019-03-05 17:43
 */
public enum EnvEnum {

    // 开发环境
    DEV(0),

    // 测试环境
    TEST(1),

    // 预发布环境
    STABLE(2),

    // 线上环境
    ONLINE(3);

    public static final String DEV_ENV = "dev";

    public static final String TEST_ENV = "test";

    public static final String STABLE_ENV = "stable";

    public static final String ONLINE_ENV = "online";


    @Getter
    private int envId;

    EnvEnum(int envId) {
        this.envId = envId;
    }

    public static EnvEnum getEnvEnum(int envId) {
        EnvEnum[] enums = EnvEnum.values();
        for (EnvEnum envEnum : enums) {
            if (envEnum.getEnvId() == envId) {
                return envEnum;
            }
        }
        return null;
    }


    /**
     * 是否包含环境
     *
     * @param envTag
     * @return
     */
    public static EnvEnum isContains(String envTag) {
        if (StringUtils.isEmpty(envTag)) {
            return null;
        }
        if (envTag.equalsIgnoreCase(DEV_ENV)) {
            return DEV;
        }
        if (envTag.equalsIgnoreCase(TEST_ENV)) {
            return TEST;
        }
        if (envTag.equalsIgnoreCase(STABLE_ENV)) {
            return STABLE;
        }
        if (envTag.equalsIgnoreCase(ONLINE_ENV)) {
            return ONLINE;
        }
        return null;
    }
}
