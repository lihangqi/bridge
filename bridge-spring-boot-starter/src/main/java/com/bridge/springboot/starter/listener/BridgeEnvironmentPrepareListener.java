package com.bridge.springboot.starter.listener;

import com.bridge.domain.BridgeConfig;
import com.bridge.processor.BridgePropertySource;
import com.bridge.processor.DefaultConfigBeanPostProcess;
import com.bridge.processor.init.BridgeEnvironment;
import com.bridge.utils.PropUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

import static com.bridge.annotation.BridgeConfigProperties.*;
import static com.bridge.annotation.BridgeConfigProperties.APP_NAME;
import static com.bridge.annotation.EnableBridgeConfig.*;

/**
 * @author Jay
 * @version v1.0
 * @description 通过事件初始化，确保在其他框架初始化之前做
 * @date 2019-11-26 18:45
 */
@Order(Ordered.LOWEST_PRECEDENCE -1)
@Slf4j
public class BridgeEnvironmentPrepareListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {


    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        BridgeConfig bridgeConfig = getBridgeConfig(environment);
        // 初始化
        BridgeEnvironment.init(bridgeConfig);
        MutablePropertySources propertySources = environment.getPropertySources();
        Properties properties = BridgePropertySource.getInstance().getProperties();
        PropertiesPropertySource source = new PropertiesPropertySource("bridgeEnvironment", properties);
        propertySources.addLast(source);
        DefaultConfigBeanPostProcess.isInit = false;
    }


    /**
     * 构造{@link BridgeConfig}
     *
     * @param environment
     * @return
     */
    private BridgeConfig getBridgeConfig(ConfigurableEnvironment environment) {
        String appCode = environment.getProperty(SPRING_BRIDGE_APP_CODE);
        String appName = environment.getProperty(SPRING_BRIDGE_APP_NAME);
        String envEnum = environment.getProperty(SPRING_BRIDGE_ENV_ENUM);
        String serverUrl = environment.getProperty(SPRING_BRIDGE_SERVER_URL);
        PropUtils.check(appCode, serverUrl, envEnum, appName);
        Properties properties = new Properties();
        properties.put(APP_CODE, appCode);
        properties.put(SERVER_URL, serverUrl);
        properties.put(ENV_ENUM, envEnum);
        properties.put(APP_NAME, appName);
        return PropUtils.buildBridgeConfig(properties);
    }


}
