package com.bridge.springboot.starter;

import com.bridge.domain.BridgeConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jay
 * @version v1.0
 * @description 属性类
 * @date 2019-01-17 14:16
 */
@Data
@ConfigurationProperties(prefix = "spring.bridge")
public class BridgeConfigProperties extends BridgeConfig {


}
