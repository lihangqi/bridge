package com.bridge.processor.init.config;

import com.bridge.domain.BridgeConfig;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.utils.rpc.RpcServiceHandler;
import com.bridge.zookeeper.BridgeNodeManager;
import com.bridge.zookeeper.ZookeeperClient;

/**
 * @author Jay
 * @version v1.0
 * @description 使用zookeeper
 * @date 2019-01-14 11:16
 */
public class ZookeeperConfigStrategy implements ConfigStrategy<BridgeConfig> {


    /**
     * 初始化的方法
     *
     * @param bridgeConfig
     */
    @Override
    public void onConfigPrepared(BridgeConfig bridgeConfig) {
        // 初始化zookeeper
        ZookeeperClient.getInstance().init(this.getZkAddress(bridgeConfig));
        ZookeeperClient.getInstance().startConnectionWithSessionConnectionWatcher(bridgeConfig.getEnvEnum());
        BridgeNodeManager.getInstance().init(bridgeConfig);
    }


    /**
     * 根据环境查询
     *
     * @param bridgeConfig
     * @return
     */
    private String getZkAddress(BridgeConfig bridgeConfig) {
        try {
            return RpcServiceHandler.getZkAddress(bridgeConfig.getServerUrl());
        } catch (Exception e) {
            throw new BridgeProcessFailException(BridgeErrorEnum.ZK_PATH_ERROR);
        }
    }
}
