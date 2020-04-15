package com.bridge.enums;

import lombok.Getter;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-23 16:28
 */
public enum EnabledStateEnum {

    // 启用
    ENABLED(1, "启用"),
    // 禁用
    NOT_ENABLED(0, "禁用");

    @Getter
    private int key;

    @Getter
    private String name;

    EnabledStateEnum(int key, String name) {
        this.key = key;
        this.name = name;
    }


}
