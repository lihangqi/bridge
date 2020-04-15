package com.bridge.console.service.zk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-02-11 15:42
 */
@Configuration
public class ZookeeperConfig {


    @Bean(initMethod = "init", destroyMethod = "destroy")
    public ZookeeperComponent zookeeperComponent() {
        return new ZookeeperComponent();
    }


}
