package com.bridge.register.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Jay
 * @version v1.0
 * @description 针对xml自定义标签属性的解析
 * @date 2019-10-31 17:50
 */
public class BridgeNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("bridge-property", new BridgePropertySourceBeanDefinitionParser());
    }
}
