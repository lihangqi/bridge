package com.bridge.mvc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.bridge.annotation.EnableBridgeConfig.*;

/**
 * @author Jay
 * @version v1.0
 * @description 在普通的spring项目中可以配置在web.xml中使用，目前是为了将配置中心的全局配置写入system property中
 * @date 2019-11-07 14:21
 */
@Slf4j
public class BridgeContextLoaderListener extends ContextLoaderListener implements ServletContextListener {


    private static final String SERVER_URL_PROPERTY_NAME = SPRING_BRIDGE_SERVER_URL;


    private static final String SERVER_ENV_PROPERTY_NAME = SPRING_BRIDGE_ENV_ENUM;


    private static final String SERVER_APP_CODE_PROPERTY_NAME = SPRING_BRIDGE_APP_CODE;


    private static final String SERVER_APP_NAME_PROPERTY_NAME = SPRING_BRIDGE_APP_NAME;


    /**
     * 需要加载的资源的路径
     */
    public static final String BRIDGE_RESOURCE_PATH = "BRIDGE-RESOURCE-PATH";


    public BridgeContextLoaderListener() {

    }

    public BridgeContextLoaderListener(WebApplicationContext context) {
        super(context);
    }


    @SuppressWarnings("ALL")
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("use BridgeContextLoaderListener init environment");
        InputStream input = null;
        try {
            String path = servletContextEvent.getServletContext().getInitParameter(BRIDGE_RESOURCE_PATH);
            if (StringUtils.isEmpty(path)) {
                log.error("未设置全局配置文件路径");
                super.contextInitialized(servletContextEvent);
                return;
            }
            input = servletContextEvent.getServletContext().getResourceAsStream(path);
            Properties properties = new Properties();
            if (input == null) {
                input = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                if (input == null) {
                    log.error("初始化配置中心的全局配置变量失败，请检查配置文件路径是否正确，如不需要请忽略");
                    super.contextInitialized(servletContextEvent);
                    return;
                }
            }
            properties.load(input);
            String serverUrl = properties.getProperty(SERVER_URL_PROPERTY_NAME);
            String appCode = properties.getProperty(SERVER_APP_CODE_PROPERTY_NAME);
            String env = properties.getProperty(SERVER_ENV_PROPERTY_NAME);
            String appName = properties.getProperty(SERVER_APP_NAME_PROPERTY_NAME);
            Properties system = System.getProperties();
            if (StringUtils.isNotEmpty(serverUrl) && !system.containsKey(SERVER_URL_PROPERTY_NAME)) {
                System.setProperty(SERVER_URL_PROPERTY_NAME, serverUrl);
            }
            if (StringUtils.isNotEmpty(appCode) && !system.containsKey(SERVER_APP_CODE_PROPERTY_NAME)) {
                System.setProperty(SERVER_APP_CODE_PROPERTY_NAME, appCode);
            }
            if (StringUtils.isNotEmpty(env) && !system.containsKey(SERVER_ENV_PROPERTY_NAME)) {
                System.setProperty(SERVER_ENV_PROPERTY_NAME, env);
            }
            if (StringUtils.isNotEmpty(appName) && !system.containsKey(SERVER_APP_NAME_PROPERTY_NAME)) {
                System.setProperty(SERVER_APP_NAME_PROPERTY_NAME, appName);
            }

        } catch (Exception e) {
            log.error("初始化配置中心的全局配置变量失败，请检查配置文件路径是否正确，如不需要请忽略", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error("初始化配置中心的全局配置变量时，流关闭异常。", e);
                }
            }
        }
        super.contextInitialized(servletContextEvent);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);
    }
}
