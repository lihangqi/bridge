package com.bridge.zookeeper.event;


import com.bridge.zookeeper.data.ConfigKeyNodeData;

/**
 * @author Jay
 * @version v1.0
 * @description 监听到节点发生变化后的事件操作
 * @date 2019-01-03 15:21
 */
public interface NodeDataChangeEvent {

    /**
     * 监听到key节点发生变化后的处理
     *
     * @param configKeyNodePath
     * @param configKeyNodeData
     */
    void doWhenConfigKeyNodeDataChanged(String configKeyNodePath, ConfigKeyNodeData configKeyNodeData);

}
