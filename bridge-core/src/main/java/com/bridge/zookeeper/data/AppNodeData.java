package com.bridge.zookeeper.data;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jay
 * @version v1.0
 * @description 应用节点
 * @date 2019-02-11 16:04
 */
@Data
public class AppNodeData implements Serializable {

    private static final long serialVersionUID = 277918222555923297L;

    /**
     * 系统名称
     */
    private String appName;

    /**
     * 系统描述
     */
    private String appDes;
}
