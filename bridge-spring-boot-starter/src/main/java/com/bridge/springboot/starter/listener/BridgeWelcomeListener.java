package com.bridge.springboot.starter.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

import static com.bridge.springboot.starter.listener.Logo.*;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-11-27 17:54
 */
@Order(LoggingApplicationListener.DEFAULT_ORDER)
public class BridgeWelcomeListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {


    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        final Logger logger = LoggerFactory.getLogger(getClass());
        String bannerText = buildBannerText();
        if (logger.isInfoEnabled()) {
            logger.info(bannerText);
        } else {
            System.out.print(bannerText);
        }
    }


    private static String buildBannerText() {

        StringBuilder bannerTextBuilder = new StringBuilder();

        bannerTextBuilder
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)
                .append(LOGO)
                .append(LINE_SEPARATOR)
                .append(" :: Bridge (v").append("2.0.0").append(") : ")
                .append(GIT_URL)
                .append(LINE_SEPARATOR)
                .append(" :: Bridge example :  ")
                .append(EXAMPLE_GIT_URL)
                .append(LINE_SEPARATOR)
                .append(" :: Bridge doc :      ")
                .append(DOC)
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)
                .append("    " + WELCOME)
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)
        ;

        return bannerTextBuilder.toString();
    }

///    public static void main(String[] args) {
//        System.out.println(buildBannerText());
//    }

}
