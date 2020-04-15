package com.bridge.console.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 拦截器配置
 * @date 2018-11-29 15:42
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    @Autowired
    private TokenValidateInterceptor tokenValidateInterceptor;

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    /**
     * 添加定义的拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = new ArrayList<>();
        excludePath.add("/index.html");
        excludePath.add("/content/**");
        excludePath.add("/static/**");
        excludePath.add("/bridge/system_manager");
        excludePath.add("/bridge/health_analysis");
        excludePath.add("/bridge/team_manager");
        excludePath.add("/bridge/welcome");
        excludePath.add("/bridge/account_info");
        excludePath.add("/bridge/config_manager");
        excludePath.add("/bridge/devDoc_manager");
        excludePath.add("/bridge/operateLog_manager");
        excludePath.add("/bridge/system_log");
        excludePath.add("/bridge/bug_feedBack");
        excludePath.add("/bridge/401");
        excludePath.add("/bridge/login");
        excludePath.add("/bridge/websocket/**");
        registry.addInterceptor(tokenValidateInterceptor).addPathPatterns("/**").excludePathPatterns(excludePath);
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**").excludePathPatterns(excludePath);
    }
}
