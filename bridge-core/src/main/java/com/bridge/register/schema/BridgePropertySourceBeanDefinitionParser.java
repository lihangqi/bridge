package com.bridge.register.schema;

import com.bridge.utils.BridgeBeanUtils;
import com.bridge.utils.PropUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.env.Environment;
import org.w3c.dom.Element;

import java.util.Properties;

import static com.bridge.annotation.BridgeConfigProperties.*;
import static com.bridge.domain.Constants.BRIDGE_CONFIG_PROPERTIES_BEAN_NAME;


/**
 * @author Jay
 * @version v1.0
 * @description 针对xml自定义标签属性的解析
 * @date 2019-10-31 17:50
 */
public class BridgePropertySourceBeanDefinitionParser extends AbstractBeanDefinitionParser {


    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        // 避免重复注册
        if (((DefaultListableBeanFactory) registry).containsBean(BRIDGE_CONFIG_PROPERTIES_BEAN_NAME)) {
            return null;
        }
        // 注册  DefaultConfigBeanPostProcess  XmlPropertySourceBuilder 用于标签的解析
        BridgeBeanUtils.registerDefaultConfigBeanPostProcess(registry);
        BridgeBeanUtils.registerBridgeConfigBeanPostProcessor(registry);
        BridgeBeanUtils.registerPropertySourcesPlaceholderConfigurer(registry);
        // 构建Properties
        Environment environment = parserContext.getDelegate().getReaderContext().getReader().getEnvironment();
        Properties properties = new Properties();
        properties.setProperty(APP_CODE, element.getAttribute(APP_CODE));
        properties.setProperty(SERVER_URL, element.getAttribute(SERVER_URL));
        properties.setProperty(ENV_ENUM, element.getAttribute(ENV_ENUM));
        properties.setProperty(APP_NAME, element.getAttribute(APP_NAME));
        // 处理占位符的问题，如果存在的话
        Properties bridgeConfig = PropUtils.resolveByPropertyResolver(properties, environment);
        BridgeBeanUtils.registerSingleton(registry, BRIDGE_CONFIG_PROPERTIES_BEAN_NAME, bridgeConfig);
        return null;
    }


    @Override
    protected boolean shouldGenerateId() {
        return true;
    }

}
