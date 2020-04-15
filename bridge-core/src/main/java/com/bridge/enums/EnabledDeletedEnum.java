package com.bridge.enums;


import lombok.Getter;

/**
 * @description: 是否启用
 * @author: Jay
 * @date: 2019-2-13 14:35:30
 * @version: V1.0
 */
public enum EnabledDeletedEnum {

    // 可删除
    ENABLED_DELETED(0, "可删除"),

    // 不可删除
    NOT_ENABLED_DELETED(1, "不可删除");

    @Getter
    private int key;

    @Getter
    private String name;

    EnabledDeletedEnum(int key, String name) {
        this.key = key;
        this.name = name;
    }
}
