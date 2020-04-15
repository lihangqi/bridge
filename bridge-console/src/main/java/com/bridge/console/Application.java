package com.bridge.console;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author Jay
 * @version v1.0
 * @description 启动器
 * @date 2018-12-29 16:19
 */
@EnableScheduling
@SpringBootApplication
@MapperScan(basePackages = "com.bridge.console.model.dao")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

