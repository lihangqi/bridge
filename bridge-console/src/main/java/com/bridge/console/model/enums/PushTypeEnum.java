package com.bridge.console.model.enums;

import com.bridge.console.utils.KeyedNamed;
import lombok.Getter;

/**
 * @author Jay
 * @version v1.0
 * @description 发布类型的枚举
 * @date 2019-02-21 16:28
 */
public enum PushTypeEnum implements KeyedNamed {

    // 灰度发布
    GRAY_SCALE(0, "灰度发布"),
    // 全量发布
    FULL(1, "全量发布"),
    ;

    @Getter
    private int key;

    @Getter
    private String name;

    PushTypeEnum(int key, String name) {
        this.key = key;
        this.name = name;
    }


}
