package com.bridge.domain;

import com.bridge.enums.EnvEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Jay
 * @version v1.0
 * @description 配置参数
 * @date 2018-12-26 15:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BridgeConfig implements Serializable {

    private static final long serialVersionUID = -1614474656996579622L;

    /**
     * 系统编码
     */
    private String appCode;

    /**
     * 服务地址
     */
    private String serverUrl;

    /**
     * 环境选择 {@link EnvEnum}
     */
    private EnvEnum envEnum;

    /**
     * 系统名称
     */
    private String appName;
}
