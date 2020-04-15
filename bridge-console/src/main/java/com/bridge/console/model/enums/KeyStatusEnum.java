package com.bridge.console.model.enums;

import com.bridge.console.utils.KeyedNamed;
import com.bridge.console.utils.result.BaseBizEnum;
import lombok.Getter;

/**
 * @author Jay
 * @version v1.0
 * @description 生效的枚举
 * @date 2019-01-23 16:28
 */
public enum KeyStatusEnum implements KeyedNamed {

    // 未生效
    NOT_ENABLED(BaseBizEnum.ZERO.getCode(), "未生效"),
    // 已生效
    ENABLED(BaseBizEnum.FIRST.getCode(), "已生效"),
    ;

    @Getter
    private int key;

    @Getter
    private String name;

    KeyStatusEnum(int key, String name) {
        this.key = key;
        this.name = name;
    }


}
