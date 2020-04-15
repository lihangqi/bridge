package com.bridge.listener;

import com.bridge.zookeeper.data.ConfigKeyNodeData;

/**
 * @author Jay
 * @version v1.0
 * @description 当值发生变化的监听
 * @date 2018-12-27 15:20
 */
public interface PropertiesChangeListener {


    /**
     * 当值发生变化的监听
     *
     * @param configKeyNodeData
     */
    void onPropertiesChanged(ConfigKeyNodeData configKeyNodeData);


    /**
     * 当值发生变化前
     *
     * @param configKeyNodeData
     */
    void onBeforePropertiesChanged(ConfigKeyNodeData configKeyNodeData);

}
