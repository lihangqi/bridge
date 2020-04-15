package com.bridge.console.model.enums;

import com.bridge.console.utils.KeyedNamed;
import com.bridge.console.utils.result.BaseBizEnum;
import lombok.Getter;

/**
 * @author Jay
 * @version v1.0
 * @description 下发状态的枚举
 * @date 2019-02-22 16:28
 */
public enum PushStatusEnum implements KeyedNamed {

    // 需要发布
    NEED_PUSH(0, "需要发布"),
    // 不需要发布
    NOT_NEED_PUSH(1, "不需要发布"),
    ;

    @Getter
    private int key;

    @Getter
    private String name;

    PushStatusEnum(int key, String name) {
        this.key = key;
        this.name = name;
    }


}
