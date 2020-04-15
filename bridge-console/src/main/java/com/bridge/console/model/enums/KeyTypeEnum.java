package com.bridge.console.model.enums;

import com.bridge.console.utils.KeyedNamed;
import com.bridge.utils.TagUtils;
import lombok.Getter;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-11-13 11:00
 */
public enum KeyTypeEnum implements KeyedNamed {


    // spring原生的注解 @Value
    ANNOTATION_VALUE(0, "@Value"),

    // spring原生的xml占位符 ${}
    XML_VALUE(1, "${}"),

    // springboot的原生注解 @ConfigurationProperties
    ANNOTATION_CONFIGURATION_PROPERTIES(2, "@ConfigurationProperties"),

    // @BridgeValue 注解
    ANNOTATION_BRIDGE_VALUE(3, "@BridgeValue"),

    // $>>{} 配置中心提供的$>>{}注解，用于xml
    XML_BRIDGE(4, TagUtils.PREFIX + TagUtils.SUFFIX),
    ;

    @Getter
    private int key;

    @Getter
    private String name;

    KeyTypeEnum(int key, String name) {
        this.key = key;
        this.name = name;
    }

}
