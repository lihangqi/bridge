package com.bridge.zookeeper.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Jay
 * @version v1.0
 * @description zk节点的数据结构
 * @date 2018-12-29 14:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigKeyNodeData implements Serializable {

    private static final long serialVersionUID = 1982323515846248627L;

    /**
     * 键
     */
    private String key;

    /**
     * 最新的键值
     */
    private String value;

    /**
     * 最新的版本号
     */
    private String version;

}
